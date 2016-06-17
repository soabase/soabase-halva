/**
 * Copyright 2016 Jordan Zimmerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.soabase.halva.processor.implicit;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import io.soabase.halva.any.AnyType;
import io.soabase.halva.implicit.ImplicitContext;
import io.soabase.halva.processor.Environment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

class ImplicitSearcher
{
    private final Environment environment;
    private final List<ContextItem> contextItems;

    static final String MAPPER_METHOD_NAME = "map";

    ImplicitSearcher(Environment environment, List<ContextItem> contextItems)
    {
        this.environment = environment;
        this.contextItems = contextItems;
    }

    FoundImplicit find(TypeMirror type)
    {
        List<FoundImplicit> foundImplicits = new ArrayList<>();
        Set<Element> mapped = new HashSet<>();
        TypeMirror erasedType = environment.getTypeUtils().erasure(type);
        contextItems.forEach(item -> {
            Element element = item.getElement();
            TypeMirror compareType = environment.typeOfFieldOrMethod(element);
            if ( environment.getTypeUtils().isAssignable(compareType, type) )
            {
                foundImplicits.add(new FoundImplicit(element));
            }
            else if ( environment.getTypeUtils().isSameType(environment.getTypeUtils().erasure(compareType), erasedType) )
            {
                mapped.add(element);
                if ( item.mapClassNeedsBuilding() )
                {
                    buildMapClass(item);
                }
            }
        });

        if ( foundImplicits.size() == 0 )
        {
            if ( mapped.size() > 0 )
            {
                foundImplicits.add(new FoundImplicit(mapped));
            }
            return null;
        }

        if ( foundImplicits.size() != 1 )
        {
            String message = (foundImplicits.size() == 0) ? "No matches found for implicit for " : "Multiple matches found for implicit for ";
            environment.error(environment.getTypeUtils().asElement(type), message + type);
            return null;
        }

        return foundImplicits.get(0);
    }

    private void buildMapClass(ContextItem item)
    {
        TypeElement typeElement = item.getClassElement();
        String packageName = environment.getPackage(typeElement);
        ClassName templateQualifiedClassName = ClassName.get(packageName, typeElement.getSimpleName().toString());
        ClassName implicitQualifiedClassName = ClassName.get(packageName, environment.getCaseClassSimpleName(typeElement, item.getAnnotationReader()));

        environment.log("Generating Context class for " + templateQualifiedClassName + " as " + implicitQualifiedClassName);

        TypeVariableName tTypeName = TypeVariableName.get("T");
        ParameterizedTypeName anyTypeName = ParameterizedTypeName.get(ClassName.get(AnyType.class), tTypeName);

        CodeBlock codeBlock = CodeBlock.builder()
            .addStatement("$T supplier = map.get(type)", ClassName.get(Supplier.class))
            .beginControlFlow("if ( supplier == null )")
            .addStatement("throw new IllegalArgumentException(\"$L\" + type)", "No specific implicit exists for ")
            .endControlFlow()
            .addStatement("return (T)supplier.get()")
            .build();

        MethodSpec methodSpec = MethodSpec.methodBuilder(MAPPER_METHOD_NAME)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            .addTypeVariable(tTypeName)
            .addParameter(ParameterSpec.builder(anyTypeName, "type").build())
            .addCode(codeBlock)
            .build()
            ;

        ParameterizedTypeName mapTypeName = ParameterizedTypeName.get(Map.class, AnyType.class, Object.class);
        CodeBlock.Builder staticBlockBuilder = CodeBlock.builder()
            .addStatement("$T workMap = new $T<>()", mapTypeName, ClassName.get(HashMap.class))
            ;
        item.getElement().getEnclosedElements().forEach(child -> {
            DeclaredType childType = environment.typeOfFieldOrMethod(child);
            if ( childType.getTypeArguments().size() > 0 )
            {
                FoundImplicit foundImplicit = find(childType);
                CodeBlock value = new ImplicitValue(environment, contextItems, foundImplicit).build();
                staticBlockBuilder.add("workMap.put(new AnyType<$T>(){}, ");
                staticBlockBuilder.add(value);
                staticBlockBuilder.addStatement(")");
            }
        });
        staticBlockBuilder.addStatement("map = $T.unmodifiableMap(workMap)", ClassName.get(Collections.class));

        FieldSpec fieldSpec = FieldSpec.builder(mapTypeName, "map").
            addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .build();

        TypeSpec.Builder builder = TypeSpec.classBuilder(implicitQualifiedClassName)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addMethod(methodSpec)
            .addStaticBlock(staticBlockBuilder.build())
            .addField(fieldSpec);
        environment.createSourceFile(packageName, templateQualifiedClassName, implicitQualifiedClassName, ImplicitContext.class.getSimpleName(), builder, typeElement);
    }
}

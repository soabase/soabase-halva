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
import io.soabase.halva.any.Any;
import io.soabase.halva.any.AnyType;
import io.soabase.halva.implicit.Implicit;
import io.soabase.halva.processor.ProcessorBase;
import io.soabase.halva.tuple.Pair;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static io.soabase.halva.comprehension.For.forComp;
import static io.soabase.halva.tuple.Tuple.Pair;

class Templates
{
    private final ProcessingEnvironment processingEnv;
    private final Map<String, String> genericsMaps = new HashMap<>();
    private final Map<String, Map<TypeMirror, Element>> specificTypesMap = new HashMap<>();

    Templates(ProcessingEnvironment processingEnv)
    {
        this.processingEnv = processingEnv;
    }

    Map<TypeMirror, Element> getSpecificTypeMap(TypeMirror type)
    {
        String key = getSpecificTypesMapKey(type);
        return specificTypesMap.computeIfAbsent(key, k -> new HashMap<>());
    }

    private String getSpecificTypesMapKey(TypeMirror type)
    {
        return processingEnv.getTypeUtils().erasure(type).toString();
    }

    void addImplicitInterface(TypeSpec.Builder builder, TypeMirror implicitInterface, List<ContextSpec> specs)
    {
        Pair<ContextSpec, ContextItem> implicit = findImplicit(implicitInterface, specs, false);
        if ( implicit == null )
        {
            return;
        }

        builder.addSuperinterface(ClassName.get(implicitInterface));
        processingEnv.getTypeUtils().asElement(implicitInterface).getEnclosedElements().forEach(implicitElement -> {
            if ( implicitElement.getKind() == ElementKind.METHOD )
            {
                ExecutableElement implicitMethod = (ExecutableElement)implicitElement;

                CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
                if ( implicitMethod.getReturnType().getKind() != TypeKind.VOID )
                {
                    codeBlockBuilder.add("return ");
                }

                applyImplicitParameter(codeBlockBuilder, implicit, specs);
                codeBlockBuilder.add(".$L(", implicitMethod.getSimpleName());
                boolean first = true;
                for ( VariableElement parameter : implicitMethod.getParameters() )
                {
                    if ( first )
                    {
                        first = false;
                    }
                    else
                    {
                        codeBlockBuilder.add(", ");
                    }
                    codeBlockBuilder.add(parameter.getSimpleName().toString());
                }
                codeBlockBuilder.addStatement(")");

                MethodSpec methodSpec = MethodSpec.overriding(implicitMethod).addCode(codeBlockBuilder.build()).build();
                builder.addMethod(methodSpec);
            }
        });
    }

    void addItem(TypeSpec.Builder builder, ExecutableElement element, List<ContextSpec> specs)
    {
        MethodSpec.Builder methodSpecBuilder = (element.getKind() == ElementKind.CONSTRUCTOR) ? MethodSpec.constructorBuilder() : MethodSpec.methodBuilder(element.getSimpleName().toString());
        methodSpecBuilder.addModifiers(element.getModifiers());
        if ( element.getReturnType().getKind() != TypeKind.VOID )
        {
            methodSpecBuilder.returns(ClassName.get(element.getReturnType()));
        }
        ProcessorBase.addTypeVariableNames(methodSpecBuilder::addTypeVariables, element.getTypeParameters());

        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        if ( element.getKind() == ElementKind.CONSTRUCTOR )
        {
            codeBlockBuilder.add("super(");
        }
        else if ( element.getReturnType().getKind() == TypeKind.VOID )
        {
            codeBlockBuilder.add("super.$L(", element.getSimpleName());
        }
        else
        {
            codeBlockBuilder.add("return super.$L(", element.getSimpleName());
        }
        AtomicInteger index = new AtomicInteger(0);
        AtomicBoolean isFirst = new AtomicBoolean(false);
        element.getParameters().forEach(parameter -> {
            if ( !isFirst.compareAndSet(false, true) )
            {
                codeBlockBuilder.add(", ");
            }
            if ( parameter.getAnnotation(Implicit.class) != null )
            {
                Pair<ContextSpec, ContextItem> implicit = findImplicit(parameter.asType(), specs, true);
                if ( (implicit != null) && implicit._2.isSpecificTypesMapMatch() )
                {
                    applyImplicitGenericMap(builder, methodSpecBuilder, codeBlockBuilder, parameter.asType(), implicit, specs, index.getAndIncrement());
                }
                else
                {
                    applyImplicitParameter(codeBlockBuilder, implicit, specs);
                }
            }
            else
            {
                ParameterSpec.Builder parameterSpec = ParameterSpec.builder(ClassName.get(parameter.asType()), parameter.getSimpleName().toString(), parameter.getModifiers().toArray(new javax.lang.model.element.Modifier[parameter.getModifiers().size()]));
                methodSpecBuilder.addParameter(parameterSpec.build());
                codeBlockBuilder.add(parameter.getSimpleName().toString());
            }
        });

        methodSpecBuilder.addCode(codeBlockBuilder.add(");\n").build());
        builder.addMethod(methodSpecBuilder.build());
    }

    private void buildGenericsMap(TypeSpec.Builder builder, ContextSpec spec, Map<TypeMirror, Element> specificTypes, String mapName, List<ContextSpec> specs)
    {
        ClassName anyName = ClassName.get(AnyType.class);
        ClassName objectName = ClassName.get(Object.class);
        ParameterizedTypeName hashMapType = ParameterizedTypeName.get(ClassName.get(HashMap.class), anyName, objectName);
        ParameterizedTypeName mapType = ParameterizedTypeName.get(ClassName.get(Map.class), anyName, objectName);

        FieldSpec fieldSpec = FieldSpec.builder(mapType, mapName, Modifier.FINAL, Modifier.PRIVATE, Modifier.STATIC).build();
        builder.addField(fieldSpec);

        String workMapName = "_work" + mapName;
        CodeBlock.Builder codeBuilder = CodeBlock.builder().addStatement("$T $L = new $T()", mapType, workMapName, hashMapType);
        specificTypes.entrySet().forEach(entry -> {
            TypeMirror type = entry.getKey();
            Element element = entry.getValue();
            ParameterizedTypeName thisAnyType = ParameterizedTypeName.get(anyName, ClassName.get(type));
            codeBuilder.add("$L.put(new $T(){}, ", workMapName, thisAnyType);
            applyImplicitParameter(codeBuilder, spec.getAnnotatedElement(), element, specs);
            codeBuilder.addStatement(")");
        });
        codeBuilder.addStatement("$L = $T.unmodifiableMap($L)", mapName, ClassName.get(Collections.class), workMapName);
        builder.addStaticBlock(codeBuilder.build());
    }

    private String generateMapKey(Map<TypeMirror, Element> items)
    {
        return items.entrySet().stream()
            .map(entry -> entry.getKey().toString() + "|" + entry.getValue().toString())
            .collect(Collectors.joining("_"));
    }

    private String checkSpecificTypesMap(TypeSpec.Builder builder, Pair<ContextSpec, ContextItem> implicit, List<ContextSpec> specs)
    {
        if ( implicit != null )
        {
            ContextItem item = implicit._2;
            if ( item.isSpecificTypesMapMatch() )
            {
                TypeMirror compareType = getCompareType(item.getElement());
                Map<TypeMirror, Element> specificTypeMap = specificTypesMap.get(getSpecificTypesMapKey(compareType));
                if ( specificTypeMap == null )
                {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Internal error. Could not find map for: " + compareType);
                    return null;
                }
                String key = generateMapKey(specificTypeMap);
                String mapName = genericsMaps.get(key);
                if ( mapName == null )
                {
                    mapName = "_genMap" + genericsMaps.size();
                    buildGenericsMap(builder, implicit._1, specificTypeMap, mapName, specs);
                    genericsMaps.put(key, mapName);
                }
                return mapName;
            }
        }
        return null;
    }

    private void applyImplicitGenericMap(TypeSpec.Builder builder, MethodSpec.Builder methodSpecBuilder, CodeBlock.Builder codeBlockBuilder, TypeMirror parameter, Pair<ContextSpec, ContextItem> implicit, List<ContextSpec> specs, int index)
    {
        String mapName = checkSpecificTypesMap(builder, implicit, specs);
        ClassName objectsName = ClassName.get(Objects.class);

        String parameterName = "type" + index;
        ParameterizedTypeName parameterType = ParameterizedTypeName.get(ClassName.get(AnyType.class), ClassName.get(parameter));
        ParameterSpec parameterSpec = ParameterSpec.builder(parameterType, parameterName).build();
        methodSpecBuilder.addParameter(parameterSpec);
        codeBlockBuilder.add("($T)$T.requireNonNull($L.get($L), $S)", parameter, objectsName, mapName, parameterName, "No specific generic type found for: " + parameter);
    }

    private void applyImplicitParameter(CodeBlock.Builder builder, Pair<ContextSpec, ContextItem> implicit, List<ContextSpec> specs)
    {
        if ( implicit == null )
        {
            builder.add("null");
        }
        else
        {
            TypeElement annotatedElement = implicit._1.getAnnotatedElement();
            Element element = implicit._2.getElement();
            applyImplicitParameter(builder, annotatedElement, element, specs);
        }
    }

    private void applyImplicitParameter(CodeBlock.Builder builder, TypeElement annotatedElement, Element element, List<ContextSpec> specs)
    {
        if ( element.getKind() == ElementKind.FIELD )
        {
            builder.add("$T.$L", annotatedElement, element.getSimpleName());
        }
        else
        {
            ExecutableElement method = (ExecutableElement)element;
            builder.add("$T.$L(", annotatedElement, method.getSimpleName());
            AtomicBoolean isFirst = new AtomicBoolean(false);
            method.getParameters().forEach(methodParameter -> {
                if ( !isFirst.compareAndSet(false, true) )
                {
                    builder.add(", ");
                }
                applyImplicitParameter(builder, findImplicit(methodParameter.asType(), specs, false), specs);
                builder.add(")");
            });
        }
    }

    private Pair<ContextSpec, ContextItem> findImplicit(TypeMirror implicitType, List<ContextSpec> specs, boolean searchSpecificTypes)
    {
        Any<ContextSpec> spec = Any.define(ContextSpec.class);
        Any<ContextItem> item = Any.define(ContextItem.class);
        List<Pair<ContextSpec, ContextItem>> matchingSpecs = forComp(spec, specs)
            .forComp(item, () -> spec.val().getItems())
            .filter(() -> {
                Element element = item.val().getElement();
                TypeMirror compareType = getCompareType(element);
                //noinspection SimplifiableIfStatement
                if ( processingEnv.getTypeUtils().isAssignable(compareType, implicitType) )
                {
                    return true;
                }

                if ( searchSpecificTypes )
                {
                    if ( processingEnv.getTypeUtils().isSameType(processingEnv.getTypeUtils().erasure(compareType), processingEnv.getTypeUtils().erasure(implicitType)) )
                    {
                        if ( specificTypesMap.containsKey(getSpecificTypesMapKey(compareType)) )
                        {
                            item.set(new ContextItem(element, true));
                            return true;
                        }
                    }
                }
                return false;
            })
            .yield(() -> Pair(spec.val(), item.val()));
        if ( matchingSpecs.size() == 1 )
        {
            return matchingSpecs.get(0);
        }

        if ( matchingSpecs.stream().allMatch(p -> p._2.isSpecificTypesMapMatch()) )
        {
            return matchingSpecs.get(0);
        }

        String message = (matchingSpecs.size() == 0) ? "No matches found for implicit for " : "Multiple matches found for implicit for ";
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message + implicitType, processingEnv.getTypeUtils().asElement(implicitType));
        return null;
    }

    private TypeMirror getCompareType(Element element)
    {
        return (element.getKind() == ElementKind.METHOD) ? ((ExecutableElement)element).getReturnType() : element.asType();
    }
}

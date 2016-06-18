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
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import io.soabase.halva.implicit.ImplicitClass;
import io.soabase.halva.implicit.Implicitly;
import io.soabase.halva.processor.Environment;
import io.soabase.halva.processor.Pass;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

class PassCreate implements Pass
{
    private final Environment environment;
    private final List<ImplicitSpec> specs;
    private final List<ContextItem> contextItems;

    PassCreate(Environment environment, List<ImplicitSpec> specs, List<ContextItem> contextItems)
    {
        this.environment = environment;
        this.specs = specs;
        this.contextItems = contextItems;
    }

    @Override
    public Optional<Pass> process()
    {
        specs.forEach(this::processOneSpec);
        return Optional.empty();
    }

    private void processOneSpec(ImplicitSpec spec)
    {
        TypeElement typeElement = spec.getAnnotatedElement();
        String packageName = environment.getPackage(typeElement);
        ClassName templateQualifiedClassName = ClassName.get(packageName, typeElement.getSimpleName().toString());
        ClassName implicitQualifiedClassName = ClassName.get(packageName, environment.getCaseClassSimpleName(typeElement, spec.getAnnotationReader()));

        environment.log("Generating ImplicitClass for " + templateQualifiedClassName + " as " + implicitQualifiedClassName);
        List<Modifier> modifiers = typeElement.getModifiers().stream().filter(m -> m != Modifier.STATIC).collect(Collectors.toList());
        TypeSpec.Builder builder = TypeSpec.classBuilder(implicitQualifiedClassName)
            .addModifiers(modifiers.toArray(new Modifier[modifiers.size()]))
            ;

        TypeMirror implicitlyType = environment.getTypeUtils().erasure(environment.getElementUtils().getTypeElement(Implicitly.class.getName()).asType());
        typeElement.getInterfaces().forEach(iface -> {
            if ( environment.getTypeUtils().isSameType(environment.getTypeUtils().erasure(iface), implicitlyType) )
            {
                DeclaredType declaredType = (DeclaredType)iface;
                if ( declaredType.getTypeArguments().size() == 1 )
                {
                    addImplicitInterface(builder, spec, declaredType.getTypeArguments().get(0));
                }
                else
                {
                    environment.error(typeElement, "Implicitly<> must have a type parameter");
                }
            }
        });

        Optional<List<TypeVariableName>> typeVariableNames = environment.addTypeVariableNames(builder::addTypeVariables, spec.getAnnotatedElement().getTypeParameters());
        if ( typeVariableNames.isPresent() )
        {
            builder.superclass(ParameterizedTypeName.get(ClassName.get(typeElement), typeVariableNames.get().toArray(new TypeName[typeVariableNames.get().size()])));
        }
        else
        {
            builder.superclass(ClassName.get(typeElement));
        }

        spec.getItems().forEach(item -> addItem(builder, spec, item));
        environment.createSourceFile(packageName, templateQualifiedClassName, implicitQualifiedClassName, ImplicitClass.class.getSimpleName(), builder, typeElement);
    }

    private void addImplicitInterface(TypeSpec.Builder builder, ImplicitSpec spec, TypeMirror implicitInterface)
    {
        FoundImplicit foundImplicit = new ImplicitSearcher(environment, spec, contextItems).find(implicitInterface);
        if ( foundImplicit == null )
        {
            return;
        }

        builder.addSuperinterface(ClassName.get(implicitInterface));
        Element classElement = environment.getTypeUtils().asElement(implicitInterface);
        classElement.getEnclosedElements().forEach(implicitElement -> {
            if ( implicitElement.getKind() == ElementKind.METHOD )
            {
                ExecutableElement implicitMethod = (ExecutableElement)implicitElement;
                addImplicitItem(builder, spec, foundImplicit, implicitInterface, implicitMethod);
            }
        });
    }

    private void addImplicitItem(TypeSpec.Builder builder, ImplicitSpec spec, FoundImplicit foundImplicit, TypeMirror implicitInterface, ExecutableElement method)
    {
        MethodSpec.Builder methodSpecBuilder = MethodSpec.overriding(method);
        if ( method.getReturnType().getKind() == TypeKind.TYPEVAR )
        {
            DeclaredType declaredType = (DeclaredType)implicitInterface;
            if ( declaredType.getTypeArguments().size() == 1 )
            {
                methodSpecBuilder.returns(ClassName.get(declaredType.getTypeArguments().get(0)));
            }
        }

        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        if ( method.getReturnType().getKind() != TypeKind.VOID )
        {
            codeBlockBuilder.add("return ");
        }
        codeBlockBuilder.add(new ImplicitValue(environment, spec, contextItems, foundImplicit).build());
        codeBlockBuilder.add(".$L(", method.getSimpleName());

        AtomicBoolean isFirst = new AtomicBoolean(true);
        method.getParameters().forEach(parameter -> {
            if ( !isFirst.compareAndSet(true, false) )
            {
                codeBlockBuilder.add(", ");
            }
            codeBlockBuilder.add(parameter.getSimpleName().toString());
        });

        methodSpecBuilder.addCode(codeBlockBuilder.addStatement(")").build());
        builder.addMethod(methodSpecBuilder.build());
    }

    private void addItem(TypeSpec.Builder builder, ImplicitSpec spec, ImplicitItem item)
    {
        ExecutableElement method = item.getExecutableElement();
        MethodSpec.Builder methodSpecBuilder = (method.getKind() == ElementKind.CONSTRUCTOR) ? MethodSpec.constructorBuilder() : MethodSpec.methodBuilder(method.getSimpleName().toString());
        methodSpecBuilder.addModifiers(method.getModifiers().stream().filter(m -> m != Modifier.ABSTRACT).collect(Collectors.toList()));
        if ( method.getReturnType().getKind() != TypeKind.VOID )
        {
            methodSpecBuilder.returns(ClassName.get(method.getReturnType()));
        }
        method.getTypeParameters().forEach(typeParameter -> methodSpecBuilder.addTypeVariable(TypeVariableName.get(typeParameter)));

        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        if ( method.getKind() == ElementKind.CONSTRUCTOR )
        {
            codeBlockBuilder.add("super(");
        }
        else if ( method.getReturnType().getKind() == TypeKind.VOID )
        {
            codeBlockBuilder.add("super.$L(", method.getSimpleName());
        }
        else
        {
            codeBlockBuilder.add("return super.$L(", method.getSimpleName());
        }

        CodeBlock methodCode = new ImplicitMethod(environment, method, spec, contextItems).build(parameter -> {
            ParameterSpec.Builder parameterSpec = ParameterSpec.builder(ClassName.get(parameter.asType()), parameter.getSimpleName().toString(), parameter.getModifiers().toArray(new javax.lang.model.element.Modifier[parameter.getModifiers().size()]));
            methodSpecBuilder.addParameter(parameterSpec.build());
        });
        codeBlockBuilder.add(methodCode);
        methodSpecBuilder.addCode(codeBlockBuilder.addStatement(")").build());
        builder.addMethod(methodSpecBuilder.build());
    }
}

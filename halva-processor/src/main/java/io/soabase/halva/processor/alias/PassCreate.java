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
package io.soabase.halva.processor.alias;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import io.soabase.halva.alias.TypeAlias;
import io.soabase.halva.alias.TypeAliasType;
import io.soabase.halva.any.AnyType;
import io.soabase.halva.processor.Environment;
import io.soabase.halva.processor.Pass;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

class PassCreate implements Pass
{
    private final Environment environment;
    private final List<AliasSpec> specs;

    PassCreate(Environment environment, List<AliasSpec> specs)
    {
        this.environment = environment;
        this.specs = specs;
    }

    @Override
    public Optional<Pass> process()
    {
        specs.forEach(this::buildFromSpec);
        return Optional.empty();
    }

    private void buildFromSpec(AliasSpec spec)
    {
        TypeElement typeElement = spec.getAnnotatedElement();
        String packageName = environment.getPackage(typeElement);
        ClassName templateQualifiedClassName = ClassName.get(packageName, typeElement.getSimpleName().toString());
        ClassName aliasQualifiedClassName = ClassName.get(packageName, environment.getGeneratedClassName(typeElement, spec.getAnnotationReader()));

        environment.log("Generating TypeAlias for " + templateQualifiedClassName + " as " + aliasQualifiedClassName);

        Collection<Modifier> modifiers = environment.getModifiers(typeElement);
        TypeName baseTypeName = ClassName.get(spec.getParameterizedType());

        TypeSpec.Builder builder = TypeSpec.interfaceBuilder(aliasQualifiedClassName)
            .addSuperinterface(baseTypeName)
            .addModifiers(modifiers.toArray(new Modifier[modifiers.size()]));

        addTypeAliasType(builder, aliasQualifiedClassName, spec.getParameterizedType());
        addDelegation(builder, aliasQualifiedClassName, spec.getParameterizedType());
        environment.createSourceFile(packageName, templateQualifiedClassName, aliasQualifiedClassName, TypeAlias.class.getSimpleName(), builder, typeElement);
    }

    private void addTypeAliasType(TypeSpec.Builder builder, ClassName aliasClassName, DeclaredType parentType)
    {
        TypeName parentTypeName = ClassName.get(parentType);
        ClassName typeAliasTypeName = ClassName.get(TypeAliasType.class);
        ClassName anyTypeName = ClassName.get(AnyType.class);
        ParameterizedTypeName typeName = ParameterizedTypeName.get(typeAliasTypeName, parentTypeName, aliasClassName);

        CodeBlock codeBlock = CodeBlock.builder()
            .add(
                "new $T<>(new $T<$T>(){}, new $T<$T>(){}, $L::$L)",
                typeAliasTypeName,
                anyTypeName,
                parentTypeName,
                anyTypeName,
                aliasClassName,
                aliasClassName.simpleName(),
                aliasClassName.simpleName()
                )
            .build();

        FieldSpec fieldSpec = FieldSpec.builder(typeName, "TypeAliasType", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            .initializer(codeBlock)
            .build();
        builder.addField(fieldSpec);
    }

    void addDelegation(TypeSpec.Builder builder, ClassName aliasClassName, DeclaredType parentType)
    {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(aliasClassName.simpleName())
            .returns(aliasClassName)
            .addParameter(ParameterSpec.builder(ClassName.get(parentType), "instance").build())
            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
            ;

        CodeBlock returnBlock = CodeBlock.builder()
            .addStatement("return $L", buildProxy(parentType, aliasClassName))
            .build();
        methodBuilder.addCode(returnBlock);

        builder.addMethod(methodBuilder.build());
    }

    private TypeSpec buildProxy(DeclaredType parentType, ClassName aliasClassName)
    {
        TypeSpec.Builder builder = TypeSpec.anonymousClassBuilder("")
            .addSuperinterface(aliasClassName);

        environment.getElementUtils().getAllMembers((TypeElement)parentType.asElement()).forEach(element -> {
            if ( element.getKind() == ElementKind.METHOD )
            {
                ExecutableElement method = (ExecutableElement)element;
                Set<Modifier> modifiers = method.getModifiers();
                if ( !modifiers.contains(Modifier.PRIVATE) && !modifiers.contains(Modifier.FINAL) && !modifiers.contains(Modifier.STATIC) && !modifiers.contains(Modifier.DEFAULT) )
                {
                    String arguments = method.getParameters().stream()
                        .map(parameter -> parameter.getSimpleName().toString())
                        .collect(Collectors.joining(", "));

                    CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
                    if ( method.getReturnType().getKind() == TypeKind.VOID )
                    {
                        codeBlockBuilder.addStatement("instance.$L($L)", method.getSimpleName(), arguments);
                    }
                    else if ( isEquals(method) )
                    {
                        Name name = method.getParameters().get(0).getSimpleName();
                        codeBlockBuilder.addStatement("return (this == $L) || instance.equals($L)", name, name);
                    }
                    else
                    {
                        codeBlockBuilder.addStatement("return instance.$L($L)", method.getSimpleName(), arguments);
                    }
                    MethodSpec methodSpec = MethodSpec.overriding(method, parentType, environment.getTypeUtils())
                        .addCode(codeBlockBuilder.build())
                        .build();
                    builder.addMethod(methodSpec);
                }
            }
        });

        return builder.build();
    }

    private boolean isEquals(ExecutableElement method)
    {
        return method.getSimpleName().toString().equals("equals")
            && (method.getReturnType().getKind() == TypeKind.BOOLEAN)
            && (method.getParameters().size() == 1)
            && method.getParameters().get(0).asType().toString().equals("java.lang.Object")
            ;
    }
}

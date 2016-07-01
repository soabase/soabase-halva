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
import io.soabase.halva.processor.GeneratedClass;
import io.soabase.halva.processor.Pass;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
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
        GeneratedClass generatedClass = environment.getGeneratedManager().resolve(typeElement);

        environment.log("Generating TypeAlias for " + generatedClass.getOriginal() + " as " + generatedClass.getGenerated());

        Collection<Modifier> modifiers = environment.getModifiers(typeElement);
        TypeName baseTypeName = ClassName.get(spec.getParameterizedType());

        TypeSpec.Builder builder;
        if ( typeElement.getKind() == ElementKind.CLASS )
        {
            builder = TypeSpec.classBuilder(generatedClass.getGenerated())
                .superclass(baseTypeName);
            addConstructorOverrides(builder, spec.getParameterizedType());
        }
        else
        {
            builder = TypeSpec.interfaceBuilder(generatedClass.getGenerated())
                .addSuperinterface(baseTypeName);
        }
        builder.addModifiers(modifiers.toArray(new Modifier[modifiers.size()]));

        addTypeAliasType(builder, generatedClass.getGenerated(), spec.getParameterizedType());
        addDelegation(builder, generatedClass.getGenerated(), spec.getParameterizedType());
        environment.createSourceFile(packageName, generatedClass.getOriginal(), generatedClass.getGenerated(), TypeAlias.class.getName(), builder, typeElement);
    }

    private void addConstructorOverrides(TypeSpec.Builder builder, DeclaredType parentType)
    {
        environment.getElementUtils().getAllMembers((TypeElement)parentType.asElement()).stream()
            .filter(element -> element.getKind() == ElementKind.CONSTRUCTOR)
            .filter(element -> element.getModifiers().contains(Modifier.PUBLIC))
            .forEach(element -> {
                ExecutableElement executableElement = (ExecutableElement)element;
                MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);
                CodeBlock.Builder constructorCodeBuilder = CodeBlock.builder().add("super(");

                ExecutableType executableType = (ExecutableType)environment.getTypeUtils().asMemberOf(parentType, executableElement);
                List<? extends TypeMirror> resolvedParameterTypes = executableType.getParameterTypes();
                int index = 0;
                for ( TypeMirror parameterType : resolvedParameterTypes )
                {
                    VariableElement variableElement = executableElement.getParameters().get(index);
                    constructorBuilder.addParameter(ParameterSpec.builder(ClassName.get(parameterType), variableElement.getSimpleName().toString()).build());
                    if ( index > 0 )
                    {
                        constructorCodeBuilder.add(", ");
                    }
                    constructorCodeBuilder.add(variableElement.getSimpleName().toString());
                    ++index;
                }
                constructorCodeBuilder.addStatement(")");
                constructorBuilder.addCode(constructorCodeBuilder.build());
                builder.addMethod(constructorBuilder.build());
            });
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
                if ( modifiers.contains(Modifier.PUBLIC) && !modifiers.contains(Modifier.NATIVE) && !modifiers.contains(Modifier.FINAL) && !modifiers.contains(Modifier.STATIC) && !modifiers.contains(Modifier.DEFAULT) )
                {
                    String arguments = method.getParameters().stream()
                        .map(parameter -> parameter.getSimpleName().toString())
                        .collect(Collectors.joining(", "));

                    MethodSpec.Builder methodSpecBuilder = MethodSpec.overriding(method, parentType, environment.getTypeUtils());
                    if ( method.getReturnType().getKind() == TypeKind.VOID )
                    {
                        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
                        codeBlockBuilder.addStatement("instance.$L($L)", method.getSimpleName(), arguments);
                        methodSpecBuilder.addCode(codeBlockBuilder.build());
                    }
                    else if ( isEquals(method) )
                    {
                        Name name = method.getParameters().get(0).getSimpleName();
                        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
                        codeBlockBuilder.addStatement("return (this == $L) || instance.equals($L)", name, name);
                        methodSpecBuilder.addCode(codeBlockBuilder.build());
                    }
                    else
                    {
                        if ( environment.getTypeUtils().isSameType(environment.getResolvedReturnType(method, parentType), parentType) )
                        {
                            CodeBlock.Builder codeBlockBuilder = CodeBlock.builder()
                                .addStatement("return $T(instance.$L($L))", aliasClassName, method.getSimpleName(), arguments);
                            methodSpecBuilder.addCode(codeBlockBuilder.build());
                            methodSpecBuilder.returns(aliasClassName);
                        }
                        else
                        {
                            CodeBlock.Builder codeBlockBuilder = CodeBlock.builder()
                                .addStatement("return instance.$L($L)", method.getSimpleName(), arguments);
                            methodSpecBuilder.addCode(codeBlockBuilder.build());
                        }
                    }

                    builder.addMethod(methodSpecBuilder.build());
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

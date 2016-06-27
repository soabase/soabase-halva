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
package io.soabase.halva.processor.comprehension;

import com.squareup.javapoet.*;
import io.soabase.halva.any.AnyVal;
import io.soabase.halva.comprehension.MonadicFor;
import io.soabase.halva.comprehension.MonadicForImpl;
import io.soabase.halva.processor.Environment;
import io.soabase.halva.processor.Pass;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

class PassCreate implements Pass
{
    private final Environment environment;
    private final List<MonadicSpec> monadicSpecs;
    private final static Set<Modifier> requiredFilterModifiers;
    static
    {
        Set<Modifier> workSet = new HashSet<>();
        workSet.add(Modifier.PUBLIC);
        requiredFilterModifiers = Collections.unmodifiableSet(workSet);
    }

    PassCreate(Environment environment, List<MonadicSpec> monadicSpecs)
    {
        this.environment = environment;
        this.monadicSpecs = monadicSpecs;
    }

    @Override
    public Optional<Pass> process()
    {
        monadicSpecs.forEach(this::buildFromSpec);
        return Optional.empty();
    }

    private void buildFromSpec(MonadicSpec spec)
    {
        TypeElement typeElement = spec.getAnnotatedElement();
        String packageName = environment.getPackage(typeElement);
        ClassName templateQualifiedClassName = ClassName.get(packageName, typeElement.getSimpleName().toString());
        ClassName generatedQualifiedClassName = ClassName.get(packageName, environment.getGeneratedClassName(typeElement, spec.getAnnotationReader()));

        environment.log("Generating " + MonadicFor.class.getSimpleName() + " for " + templateQualifiedClassName + " as " + generatedQualifiedClassName);

        SpecData specData = new SpecData(spec);
        boolean hasFilter = hasFilter(spec);

        Collection<Modifier> modifiers = environment.getModifiers(typeElement);
        TypeSpec.Builder builder = TypeSpec.classBuilder(generatedQualifiedClassName)
            .addModifiers(modifiers.toArray(new Modifier[modifiers.size()]));

        addConstructorAndDelegate(builder, spec);
        addStaticBuilder(builder, spec, generatedQualifiedClassName);
        addForComp(builder, generatedQualifiedClassName, specData);
        addYield(builder, specData);
        addLetComp(builder, generatedQualifiedClassName);
        if ( hasFilter )
        {
            addFilter(builder, generatedQualifiedClassName);
        }

        environment.createSourceFile(packageName, templateQualifiedClassName, generatedQualifiedClassName, MonadicFor.class.getName(), builder, typeElement);
    }

    private boolean hasFilter(MonadicSpec spec)
    {
        return spec.getAnnotatedElement().getEnclosedElements().stream().anyMatch(element -> {
            if ( element.getKind() == ElementKind.METHOD )
            {
                ExecutableElement method = (ExecutableElement)element;
                if ( method.getSimpleName().toString().equals("filter") )
                {
                    if ( method.getModifiers().equals(requiredFilterModifiers) )
                    {
                        if ( method.getReturnType().getKind() != TypeKind.VOID )
                        {
                            if ( method.getParameters().size() == 2 )
                            {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        });
    }

    private void addFilter(TypeSpec.Builder builder, ClassName generatedQualifiedClassName)
    {
        ParameterizedTypeName supplierName = ParameterizedTypeName.get(ClassName.get(Supplier.class), ClassName.get(Boolean.class));

        CodeBlock.Builder codeBuilder = CodeBlock.builder()
            .addStatement("delegate.filter(supplier)")
            .addStatement("return this")
            ;

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("filter")
            .addModifiers(Modifier.PUBLIC)
            .returns(generatedQualifiedClassName)
            .addParameter(ParameterSpec.builder(supplierName, "supplier").build())
            .addCode(codeBuilder.build())
            ;

        builder.addMethod(methodBuilder.build());
    }

    private void addLetComp(TypeSpec.Builder builder, ClassName generatedQualifiedClassName)
    {
        ParameterizedTypeName supplierName = ParameterizedTypeName.get(ClassName.get(Supplier.class), TypeVariableName.get("R"));
        ParameterizedTypeName anyName = ParameterizedTypeName.get(ClassName.get(AnyVal.class), TypeVariableName.get("R"));

        CodeBlock.Builder codeBuilder = CodeBlock.builder()
            .addStatement("delegate.letComp(any, supplier)")
            .addStatement("return this")
            ;

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("letComp")
            .addModifiers(Modifier.PUBLIC)
            .returns(generatedQualifiedClassName)
            .addTypeVariable(TypeVariableName.get("R"))
            .addParameter(ParameterSpec.builder(anyName, "any").build())
            .addParameter(ParameterSpec.builder(supplierName, "supplier").build())
            .addCode(codeBuilder.build())
            ;

        builder.addMethod(methodBuilder.build());
    }

    private void addYield(TypeSpec.Builder builder, SpecData specData)
    {
        ParameterizedTypeName supplierName = ParameterizedTypeName.get(ClassName.get(Supplier.class), specData.monadicTypeName);

        CodeBlock.Builder codeBuilder = CodeBlock.builder()
            .addStatement("return delegate.yield(supplier)");

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("yield")
            .addModifiers(Modifier.PUBLIC)
            .returns(specData.parameterizedMonadicName)
            .addTypeVariables(specData.typeVariableNames)
            .addParameter(ParameterSpec.builder(supplierName, "supplier").build())
            .addAnnotation(AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "\"unchecked\"").build())
            .addCode(codeBuilder.build())
            ;

        builder.addMethod(methodBuilder.build());
    }

    private void addForComp(TypeSpec.Builder builder, ClassName generatedQualifiedClassName, SpecData specData)
    {
        ParameterizedTypeName supplierName = ParameterizedTypeName.get(ClassName.get(Supplier.class), WildcardTypeName.subtypeOf(specData.parameterizedMonadicName));

        CodeBlock.Builder codeBuilder = CodeBlock.builder()
            .addStatement("delegate.forComp(any, supplier)")
            .addStatement("return this");

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("forComp")
            .addModifiers(Modifier.PUBLIC)
            .returns(generatedQualifiedClassName)
            .addTypeVariables(specData.typeVariableNames)
            .addParameter(ParameterSpec.builder(specData.anyName, "any").build())
            .addParameter(ParameterSpec.builder(supplierName, "supplier").build())
            .addCode(codeBuilder.build())
            ;

        builder.addMethod(methodBuilder.build());
    }

    private void addStaticBuilder(TypeSpec.Builder builder, MonadicSpec spec, ClassName generatedQualifiedClassName)
    {
        CodeBlock.Builder codeBuilder = CodeBlock.builder()
            .addStatement("return new $T(new $T<>(new $T()))", generatedQualifiedClassName, ClassName.get(MonadicForImpl.class), spec.getAnnotatedElement());

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("start")
            .returns(generatedQualifiedClassName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addCode(codeBuilder.build())
            ;

        builder.addMethod(methodBuilder.build());
    }

    private void addConstructorAndDelegate(TypeSpec.Builder builder, MonadicSpec spec)
    {
        ClassName monadicForName = ClassName.get(MonadicForImpl.class);
        ParameterizedTypeName parameterizedMonadicName = ParameterizedTypeName.get(monadicForName, ClassName.get(spec.getMonadElement()));

        FieldSpec.Builder delegate = FieldSpec.builder(parameterizedMonadicName, "delegate", Modifier.PRIVATE, Modifier.FINAL);
        builder.addField(delegate.build());

        MethodSpec.Builder methodBuilder = MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PRIVATE).addParameter(ParameterSpec.builder(parameterizedMonadicName, "delegate").build())
            .addCode(CodeBlock.builder().addStatement("this.delegate = delegate").build());

        builder.addMethod(methodBuilder.build());
    }
}

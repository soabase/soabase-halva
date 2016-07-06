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
package io.soabase.halva.processor.caseclass;

import com.squareup.javapoet.*;
import io.soabase.halva.any.Any;
import io.soabase.halva.any.AnyClassTuple;
import io.soabase.halva.any.AnyVal;
import io.soabase.halva.processor.Environment;
import io.soabase.halva.tuple.Tuple;
import io.soabase.halva.tuple.details.Tuple0;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Templates
{
    private final Initializers initializers;
    private final Environment environment;

    Templates(Environment environment)
    {
        initializers = new Initializers(environment);
        this.environment = environment;
    }

    void addField(CaseClassItem item, TypeSpec.Builder builder, TypeName type, boolean makeFinal, boolean makeVolatile, Settings settings)
    {
        TypeName localType;
        if ( makeFinal )
        {
            localType = type;
        }
        else
        {
            localType = item.hasDefaultValue() ? type.box() : type;
        }
        FieldSpec.Builder fieldBuilder = FieldSpec.builder(localType, item.getName(), Modifier.PRIVATE);
        if ( makeFinal )
        {
            fieldBuilder.addModifiers(Modifier.FINAL);
        }
        if ( makeVolatile )
        {
            fieldBuilder.addModifiers(Modifier.VOLATILE);
        }
        if ( settings.json && !checkParentJsonAnnotations(item.getElement(), fieldBuilder) )
        {
            AnnotationSpec annotationSpec = AnnotationSpec.builder(ClassName.get("com.fasterxml.jackson.annotation", "JsonProperty")).build();
            fieldBuilder.addAnnotation(annotationSpec);
        }
        if ( settings.beanValidation )
        {
            item.getElement().getAnnotationMirrors().stream()
                .filter(this::checkExternalAnnotation)
                .map(AnnotationSpec::get)
                .forEach(fieldBuilder::addAnnotation);
        }
        builder.addField(fieldBuilder.build());
    }

    void addGetter(CaseClassItem item, TypeSpec.Builder builder, TypeName type, Modifier... modifiers)
    {
        MethodSpec methodSpec = MethodSpec
            .methodBuilder(item.getName())
            .returns(type)
            .addModifiers(modifiers)
            .addAnnotation(Override.class)
            .addStatement("return $L", item.getName())
            .build();

        builder.addMethod(methodSpec);
    }

    void addSetter(CaseClassItem item, TypeSpec.Builder builder, TypeName type, Modifier... modifiers)
    {
        ParameterSpec parameterSpec = ParameterSpec.builder(type, item.getName()).build();

        MethodSpec methodSpec = MethodSpec
            .methodBuilder(item.getName())
            .returns(TypeName.VOID)
            .addModifiers(modifiers)
            .addParameter(parameterSpec)
            .addStatement("this.$L = $L", item.getName(), item.getName())
            .build();

        builder.addMethod(methodSpec);
    }

    void addBuilderSetter(CaseClassItem item, TypeSpec.Builder builder, TypeName type, TypeName builderClassName, Modifier... modifiers)
    {
        ParameterSpec parameterSpec = ParameterSpec.builder(type, item.getName()).build();

        MethodSpec methodSpec = MethodSpec
            .methodBuilder(item.getName())
            .returns(builderClassName)
            .addModifiers(modifiers)
            .addParameter(parameterSpec)
            .addStatement("this.$L = $L", item.getName(), item.getName())
            .addStatement("return this")
            .build();

        builder.addMethod(methodSpec);
    }

    void addGetterItem(CaseClassItem item, TypeSpec.Builder builder, Settings settings)
    {
        TypeName type = environment.getGeneratedManager().toTypeName(item.getType());
        addField(item, builder, type, true, false, settings);
        addGetter(item, builder, type, Modifier.PUBLIC);
    }

    void addSetterItem(CaseClassItem item, TypeSpec.Builder builder, Settings settings)
    {
        TypeName type = environment.getGeneratedManager().toTypeName(item.getType());
        addField(item, builder, type, false, true, settings);
        addGetter(item, builder, type, Modifier.PUBLIC);
        addSetter(item, builder, type, Modifier.PUBLIC);
    }

    void addItem(CaseClassItem item, TypeSpec.Builder builder, Settings settings)
    {
        if ( item.isMutable() )
        {
            addSetterItem(item, builder, settings);
        }
        else
        {
            addGetterItem(item, builder, settings);
        }
    }

    void addBuilderSetterItem(CaseClassItem item, TypeSpec.Builder builder, TypeName builderClassName, Settings settings)
    {
        TypeName type = environment.getGeneratedManager().toTypeName(item.getType());
        addField(item, builder, type, false, false, settings);
        addBuilderSetter(item, builder, type, builderClassName, Modifier.PUBLIC);
    }

    void addConstructor(CaseClassSpec spec, TypeSpec.Builder builder)
    {
        MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
            .addCode(buildFieldValidation(spec))
            .addModifiers(Modifier.PROTECTED);
        spec.getItems().stream()
            .forEach(item -> {
                TypeName type = environment.getGeneratedManager().toTypeName(item.getType());
                constructor.addParameter(item.hasDefaultValue() ? type.box() : type, item.getName());
                constructor.addStatement("this.$L = $L", item.getName(), item.getName());
            });
        builder.addMethod(constructor.build());
    }

    void addTuple(CaseClassSpec spec, TypeSpec.Builder builder)
    {
        Optional<Class<? extends Tuple>> optionalTupleClass = Tuple.getTupleClass(spec.getItems().size());
        boolean hasThisTuple = optionalTupleClass.isPresent();
        Class<? extends Tuple> tupleClass = optionalTupleClass.orElse(Tuple.class);

        TypeName typeName;
        CodeBlock codeBlock;
        if ( hasThisTuple )
        {
            List<TypeName> typeNameList = spec.getItems().stream()
                .map(item -> environment.getGeneratedManager().toTypeName(item.getType()).box())
                .collect(Collectors.toList());
            typeName = getTupleType(tupleClass, typeNameList);

            String args = spec.getItems().stream()
                .map(item -> item.getName() + "()")
                .collect(Collectors.joining(", "));

            codeBlock = CodeBlock.builder()
                .addStatement("return $T.Tu($L)", Tuple.class, args)
                .build();
        }
        else
        {
            typeName = ClassName.get(tupleClass);

            codeBlock = CodeBlock.builder()
                .addStatement("throw new $T($S)", UnsupportedOperationException.class, "Too many arguments for a Tuple")
                .build();
        }

        MethodSpec methodSpec = MethodSpec.methodBuilder("tuple")
            .returns(typeName)
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .addCode(codeBlock)
            .build();
        builder.addMethod(methodSpec);
    }

    void addHashCode(CaseClassSpec spec, TypeSpec.Builder builder)
    {
        MethodSpec.Builder hashCodeBuilder = MethodSpec
            .methodBuilder("hashCode")
            .returns(TypeName.INT)
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC);
        boolean firstTime = true;
        for ( CaseClassItem item : spec.getItems() )
        {
            String field;
            if ( item.getType().getKind() == TypeKind.BOOLEAN )
            {
                field = "(" + item.getName() + " ? 1 : 0)";
            }
            else if ( item.getType().getKind() == TypeKind.FLOAT )
            {
                field = "Float.hashCode(" + item.getName() + ")";
            }
            else if ( item.getType().getKind() == TypeKind.DOUBLE )
            {
                field = "Double.hashCode(" + item.getName() + ")";
            }
            else if ( item.getType().getKind() == TypeKind.LONG )
            {
                field = "Long.hashCode(" + item.getName() + ")";
            }
            else if ( item.getType().getKind().isPrimitive() )
            {
                field = item.getName();
            }
            else
            {
                field = item.getName() + ".hashCode()";
            }
            String format = firstTime ? "int result = $L" : "result = 31 * result + $L";
            hashCodeBuilder.addStatement(format, field);
            firstTime = false;
        }
        if ( spec.getItems().size() > 0 )
        {
            hashCodeBuilder.addStatement("return result");
        }
        else
        {
            hashCodeBuilder.addStatement("return super.hashCode()");
        }
        builder.addMethod(hashCodeBuilder.build());
    }

    void addEquals(CaseClassSpec spec, TypeSpec.Builder builder, ClassName className)
    {
        MethodSpec.Builder equalsBuilder = MethodSpec
            .methodBuilder("equals")
            .returns(TypeName.BOOLEAN)
            .addParameter(ParameterSpec.builder(Object.class, "rhsObj").build())
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC);

        equalsBuilder.beginControlFlow("if ( this == rhsObj )")
            .addStatement("return true")
            .endControlFlow();

        equalsBuilder.beginControlFlow("if ( rhsObj == null || getClass() != rhsObj.getClass() )")
            .addStatement("return false")
            .endControlFlow();

        if ( spec.getItems().size() > 0 )
        {
            equalsBuilder.addStatement("$L rhs = ($L)rhsObj", className.simpleName(), className.simpleName());
        }

        spec.getItems().forEach(item -> {
            if ( item.getType().getKind().isPrimitive() )
            {
                equalsBuilder.beginControlFlow("if ( $L != rhs.$L )", item.getName(), item.getName())
                    .addStatement("return false")
                    .endControlFlow();
            }
            else
            {
                equalsBuilder.beginControlFlow("if ( !$L.equals(rhs.$L) )", item.getName(), item.getName())
                    .addStatement("return false")
                    .endControlFlow();
            }
        });
        equalsBuilder.addStatement("return true");
        builder.addMethod(equalsBuilder.build());
    }

    void addDebugString(CaseClassSpec spec, TypeSpec.Builder builder, ClassName className)
    {
        MethodSpec.Builder toStringBuilder = MethodSpec
            .methodBuilder("debugString")
            .returns(String.class)
            .addModifiers(Modifier.PUBLIC);

        toStringBuilder.addCode("return \"$L { \" +\n", className.simpleName());
        spec.getItems().forEach(item -> toDebugStringItem(toStringBuilder, item));
        toStringBuilder.addStatement("'}'");
        builder.addMethod(toStringBuilder.build());
    }

    void addToString(CaseClassSpec spec, TypeSpec.Builder builder, ClassName className)
    {
        MethodSpec.Builder toStringBuilder = MethodSpec
            .methodBuilder("toString")
            .returns(String.class)
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC);

        toStringBuilder.addCode("return \"$L(\" +\n", className.simpleName());
        boolean isFirst = true;
        for ( CaseClassItem item : spec.getItems() )
        {
            toStringItem(toStringBuilder, item, isFirst);
            isFirst = false;
        }
        toStringBuilder.addStatement("')'");
        builder.addMethod(toStringBuilder.build());
    }

    void addObjectInstance(TypeSpec.Builder builder, ClassName className, Optional<List<TypeVariableName>> typeVariableNames)
    {
        TypeName localCaseClassName = getLocalCaseClassName(className, typeVariableNames);
        FieldSpec.Builder fieldSpecBuilder = FieldSpec.builder(className, className.simpleName(), Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
            .initializer("new $T()", localCaseClassName);
        builder.addField(fieldSpecBuilder.build());
    }

    void addApplyBuilder(CaseClassSpec spec, TypeSpec.Builder builder, ClassName className, Optional<List<TypeVariableName>> typeVariableNames)
    {
        TypeName localCaseClassName = getLocalCaseClassName(className, typeVariableNames);

        List<ParameterSpec> parameters = spec.getItems().stream()
            .map(item -> ParameterSpec.builder(environment.getGeneratedManager().toTypeName(item.getType()), item.getName()).build())
            .collect(Collectors.toList());

        String arguments = spec.getItems().stream().map(CaseClassItem::getName).collect(Collectors.joining(", "));
        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder()
            .addStatement("return new $L$L($L)", className.simpleName(), getDuck(typeVariableNames), arguments);

        MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(className.simpleName())
            .returns(localCaseClassName)
            .addParameters(parameters)
            .addCode(codeBlockBuilder.build())
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        if ( typeVariableNames.isPresent() )
        {
            methodSpecBuilder.addTypeVariables(typeVariableNames.get());
        }
        builder.addMethod(methodSpecBuilder.build());
    }

    void addBuilder(CaseClassSpec spec, TypeSpec.Builder builder, ClassName className, Settings settings, Optional<List<TypeVariableName>> typeVariableNames)
    {
        TypeName builderClassName = getBuilderClassName(className, typeVariableNames);
        TypeSpec typeSpec = buildBuilderClass(spec, className, builderClassName, settings, typeVariableNames);
        MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("builder")
            .returns(builderClassName)
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addStatement("return new Builder$L()", getDuck(typeVariableNames));

        if ( typeVariableNames.isPresent() )
        {
            methodSpecBuilder.addTypeVariables(typeVariableNames.get());
        }

        builder.addMethod(methodSpecBuilder.build());
        builder.addType(typeSpec);
    }

    TypeName getBuilderClassName(ClassName className, Optional<List<TypeVariableName>> typeVariableNames)
    {
        ClassName rawClassname = className.nestedClass("Builder");
        if ( typeVariableNames.isPresent() )
        {
            return ParameterizedTypeName.get(rawClassname, typeVariableNames.get().toArray(new TypeName[typeVariableNames.get().size()]));
        }
        return rawClassname;
    }

    void addCopy(TypeSpec.Builder builder, ClassName className, Optional<List<TypeVariableName>> typeVariableNames)
    {
        TypeName builderClassName = getBuilderClassName(className, typeVariableNames);
        MethodSpec copySpec = MethodSpec
            .methodBuilder("copy")
            .returns(builderClassName)
            .addCode(CodeBlock.builder().addStatement("return new Builder$L(this)", getDuck(typeVariableNames)).build())
            .addModifiers(Modifier.PUBLIC)
            .build();
        builder.addMethod(copySpec);
    }

    void addClassTuple(CaseClassSpec spec, TypeSpec.Builder builder, ClassName className, Settings settings, Optional<List<TypeVariableName>> typeVariableNames)
    {
        addClassTupleMethods(spec, builder, className, typeVariableNames);
        addClassTuplable(spec, builder, className, settings);
    }

    private void addClassTupleMethods(CaseClassSpec spec, TypeSpec.Builder builder, ClassName className, Optional<List<TypeVariableName>> typeVariableNames)
    {
        Optional<Class<? extends Tuple>> optionalTupleClass = Tuple.getTupleClass(spec.getItems().size());
        if ( !optionalTupleClass.isPresent() )
        {
            return;
        }

        ClassName anyClassName = ClassName.get(Any.class);
        ClassName matchClassName = ClassName.get(AnyVal.class);
        ClassName anyClassTupleName = ClassName.get(AnyClassTuple.class);
        TypeName localCaseClassName = getLocalCaseClassName(className, typeVariableNames);
        TypeName classTupleClassName = ParameterizedTypeName.get(anyClassTupleName, localCaseClassName);

        CodeBlock.Builder returnCode = CodeBlock.builder().add("return new $T($T.Tu(", classTupleClassName, Tuple.class);
        IntStream.range(0, spec.getItems().size()).forEach(i -> {
            CaseClassItem item = spec.getItems().get(i);
            if ( i > 0 )
            {
                returnCode.add(", ");
            }
            returnCode.add("$T.loose($L)", anyClassName, item.getName());
        });
        spec.getItems().forEach(item -> {
        });
        returnCode.addStatement(")){}");

        MethodSpec.Builder tupleMethod = MethodSpec
            .methodBuilder(getClassTupleMethodName(className))
            .returns(classTupleClassName)
            .addCode(returnCode.build())
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        spec.getItems().forEach(item -> {
            TypeName mainType = null;
            if ( item.getType().getKind() == TypeKind.DECLARED )
            {
                DeclaredType declaredType = (DeclaredType)item.getType();
                List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
                if ( typeArguments.size() > 0 )
                {
                    TypeName[] typeNames = new TypeName[typeArguments.size()];
                    for ( int i = 0; i < typeArguments.size(); ++i )
                    {
                        typeNames[i] = WildcardTypeName.subtypeOf(TypeName.get(typeArguments.get(i)).box());
                    }
                    mainType = ParameterizedTypeName.get(ClassName.get((TypeElement)declaredType.asElement()), typeNames);
                }
            }
            if ( mainType == null )
            {
                mainType = TypeName.get(item.getType()).box();
            }
            TypeName wildcareType = WildcardTypeName.subtypeOf(mainType);
            ParameterizedTypeName type = ParameterizedTypeName.get(matchClassName, wildcareType);
            tupleMethod.addParameter(type, item.getName());
        });

        if ( typeVariableNames.isPresent() )
        {
            tupleMethod.addTypeVariables(typeVariableNames.get());
        }

        builder.addMethod(tupleMethod.build());
    }

    private String getClassTupleMethodName(ClassName className)
    {
        return className.simpleName() + "Any";
    }

    private void addClassTuplable(CaseClassSpec spec, TypeSpec.Builder builder, ClassName className, Settings settings)
    {
        CodeBlock anyVal = CodeBlock.of("$T.any()", Any.class);
        CodeBlock.Builder initialize = CodeBlock.builder().add("$L(", getClassTupleMethodName(className));
        IntStream.range(0, spec.getItems().size())
            .forEach(i -> {
                if ( i > 0 )
                {
                    initialize.add(", ");
                }
                initialize.add(anyVal);
            });
        initialize.add(").getClass()");
        FieldSpec.Builder fieldSpecBuilder = FieldSpec.builder(Class.class, "classTuplableClass", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer(initialize.build());

        MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("getClassTuplableClass")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(Class.class)
            .addCode(CodeBlock.builder().addStatement("return classTuplableClass").build())
            ;
        if ( settings.json )
        {
            methodSpecBuilder.addAnnotation(ClassName.get("com.fasterxml.jackson.annotation", "JsonIgnore"));
        }
        builder.addMethod(methodSpecBuilder.build());
        builder.addField(fieldSpecBuilder.build());
    }

    private void toStringItem(MethodSpec.Builder toStringBuilder, CaseClassItem item, boolean isFirst)
    {
        if ( item.getType().toString().equals(String.class.getName()) )
        {
            toStringBuilder.addCode("\"$L\\\"\" + $L + \"\\\"\" + \n", isFirst ? "" : ", ", item.getName());
        }
        else
        {
            toStringBuilder.addCode("$L$L +\n", isFirst ? "" : "\", \" + ", item.getName());
        }
    }

    private void toDebugStringItem(MethodSpec.Builder toStringBuilder, CaseClassItem item)
    {
        if ( item.getType().toString().equals(String.class.getName()) )
        {
            toStringBuilder.addCode("    \"$L=\\\"\" + $L + \"\\\"; \" +\n", item.getName(), item.getName());
        }
        else
        {
            toStringBuilder.addCode("    \"$L=\" + $L + \"; \" +\n", item.getName(), item.getName());
        }
    }

    private TypeName getTupleType(Class<? extends Tuple> tupleClass, List<TypeName> itemTypes)
    {
        TypeName tupleType;
        if ( Tuple0.class.isAssignableFrom(tupleClass) )
        {
            tupleType = ClassName.get(Tuple0.class);
        }
        else
        {
            tupleType = ParameterizedTypeName.get(ClassName.get(tupleClass), itemTypes.toArray(new TypeName[itemTypes.size()]));
        }
        return tupleType;
    }

    private String getDuck(Optional<List<TypeVariableName>> typeVariableNames)
    {
        return typeVariableNames.isPresent() ? "<>" : "";
    }

    private CodeBlock buildFieldValidation(CaseClassSpec spec)
    {
        CodeBlock.Builder codeBuilder = CodeBlock.builder();
        spec.getItems().forEach(item -> initializers.addTo(codeBuilder, spec, item));
        return codeBuilder.build();
    }

    private TypeSpec buildBuilderClass(CaseClassSpec spec, ClassName caseClassName, TypeName builderClassName, Settings settings, Optional<List<TypeVariableName>> typeVariableNames)
    {
        TypeName localCaseClassName = getLocalCaseClassName(caseClassName, typeVariableNames);
        CodeBlock.Builder newBuilder = CodeBlock.builder().add("return new $L$L(", caseClassName.simpleName(), getDuck(typeVariableNames));
        String comma = "";
        for ( CaseClassItem item : spec.getItems() )
        {
            newBuilder.add("$L\n    $L", comma, item.getName());
            comma = ", ";
        }
        newBuilder.add("\n);\n");

        MethodSpec.Builder newSpecBuilder = MethodSpec.methodBuilder("build")
            .returns(localCaseClassName)
            .addModifiers(Modifier.PUBLIC)
            .addCode(newBuilder.build())
            ;

        MethodSpec.Builder constructorSpecBuilder = MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PRIVATE)
            ;
        if ( settings.json )
        {
            AnnotationSpec annotationSpec = AnnotationSpec.builder(ClassName.get("com.fasterxml.jackson.annotation", "JsonCreator")).build();
            constructorSpecBuilder.addAnnotation(annotationSpec);
        }

        MethodSpec.Builder copyConstructorBuilder = MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PRIVATE)
            .addParameter(ParameterSpec.builder(localCaseClassName, "rhs").build());
        spec.getItems().forEach(item -> copyConstructorBuilder.addStatement("$L = rhs.$L", item.getName(), item.getName()));

        TypeSpec.Builder builder = TypeSpec.classBuilder("Builder")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
            .addMethod(constructorSpecBuilder.build())
            .addMethod(copyConstructorBuilder.build())
            .addMethod(newSpecBuilder.build());
        if ( settings.json )
        {
            AnnotationSpec annotationSpec = AnnotationSpec.builder(ClassName.get("com.fasterxml.jackson.databind.annotation", "JsonPOJOBuilder"))
                .addMember("withPrefix", "\"\"")
                .build();
            builder.addAnnotation(annotationSpec);
        }
        if ( typeVariableNames.isPresent() )
        {
            builder.addTypeVariables(typeVariableNames.get());
        }

        spec.getItems().forEach(item -> addBuilderSetterItem(item, builder, builderClassName, settings));

        return builder.build();
    }

    private TypeName getLocalCaseClassName(ClassName caseClassName, Optional<List<TypeVariableName>> typeVariableNames)
    {
        TypeName localCaseClassName;
        if ( typeVariableNames.isPresent() )
        {
            localCaseClassName = ParameterizedTypeName.get(caseClassName, typeVariableNames.get().toArray(new TypeName[typeVariableNames.get().size()]));
        }
        else
        {
            localCaseClassName = caseClassName;
        }
        return localCaseClassName;
    }

    private boolean checkParentJsonAnnotations(Element element, FieldSpec.Builder fieldBuilder)
    {
        return element.getAnnotationMirrors().stream().filter(annotation -> {
            if ( annotation.getAnnotationType().asElement().toString().equals("com.fasterxml.jackson.annotation.JsonProperty")
                || annotation.getAnnotationType().asElement().toString().equals("com.fasterxml.jackson.annotation.JsonIgnore") )
            {
                AnnotationSpec.Builder annotationSpec = AnnotationSpec.builder(ClassName.get((TypeElement)annotation.getAnnotationType().asElement()));
                annotation.getElementValues().entrySet().forEach(entry ->
                    annotationSpec.addMember(entry.getKey().getSimpleName().toString(), "$S", entry.getValue().getValue()));
                fieldBuilder.addAnnotation(annotationSpec.build());
                return true;
            }
            return false;
        }).count() > 0;
    }

    private boolean checkExternalAnnotation(AnnotationMirror annotation)
    {
        String path = annotation.getAnnotationType().asElement().toString();

        return path.startsWith("io.soabase.halva.caseclass.")
            || path.startsWith("com.fasterxml.jackson.annotation.") ? false : true;
    }
}

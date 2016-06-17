package io.soabase.halva.processor.implicit2;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import io.soabase.halva.implicit.ImplicitClass;
import io.soabase.halva.processor.Environment;
import io.soabase.halva.processor.Pass;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import java.util.List;
import java.util.Optional;
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
        //spec.getAnnotationReader().getClasses("implicitInterfaces").forEach(clazz -> templates.addImplicitInterface(builder, clazz, specs));

        Optional<List<TypeVariableName>> typeVariableNames = environment.addTypeVariableNames(builder::addTypeVariables, spec.getAnnotatedElement().getTypeParameters());
        if ( typeVariableNames.isPresent() )
        {
            builder.superclass(ParameterizedTypeName.get(ClassName.get(typeElement), typeVariableNames.get().toArray(new TypeName[typeVariableNames.get().size()])));
        }
        else
        {
            builder.superclass(ClassName.get(typeElement));
        }

        spec.getItems().forEach(item -> addItem(builder, item));
        environment.createSourceFile(packageName, templateQualifiedClassName, implicitQualifiedClassName, ImplicitClass.class.getSimpleName(), builder, typeElement);
    }

    private void addItem(TypeSpec.Builder builder, ImplicitItem item)
    {
        ExecutableElement method = item.getExecutableElement();
        MethodSpec.Builder methodSpecBuilder = (method.getKind() == ElementKind.CONSTRUCTOR) ? MethodSpec.constructorBuilder() : MethodSpec.methodBuilder(method.getSimpleName().toString());
        methodSpecBuilder.addModifiers(method.getModifiers());
        if ( method.getReturnType().getKind() != TypeKind.VOID )
        {
            methodSpecBuilder.returns(ClassName.get(method.getReturnType()));
        }
        environment.addTypeVariableNames(methodSpecBuilder::addTypeVariables, method.getTypeParameters());
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
        CodeBlock methodCode = new ImplicitMethod(environment, method, contextItems).build();
        codeBlockBuilder.add(methodCode);
        methodSpecBuilder.addCode(codeBlockBuilder.addStatement(")").build());

        builder.addMethod(methodSpecBuilder.build());
    }
}

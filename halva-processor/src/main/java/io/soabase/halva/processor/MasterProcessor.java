package io.soabase.halva.processor;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import io.soabase.halva.alias.TypeAlias;
import io.soabase.halva.caseclass.CaseClass;
import io.soabase.halva.caseclass.CaseObject;
import io.soabase.halva.comprehension.MonadicFor;
import io.soabase.halva.implicit.ImplicitClass;
import io.soabase.halva.implicit.ImplicitContext;
import io.soabase.halva.processor.alias.AliasPassFactory;
import io.soabase.halva.processor.caseclass.CaseClassPassFactory;
import io.soabase.halva.processor.comprehension.MonadicForPassFactory;
import io.soabase.halva.processor.implicit.ImplicitPassFactory;
import javax.annotation.Generated;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static io.soabase.halva.sugar.Sugar.Map;
import static io.soabase.halva.tuple.Tuple.Pair;

@SupportedAnnotationTypes({
    "io.soabase.halva.caseclass.CaseClass",
    "io.soabase.halva.caseclass.CaseObject",
    "io.soabase.halva.alias.TypeAlias",
    "io.soabase.halva.comprehension.MonadicFor",
    "io.soabase.halva.implicit.ImplicitClass",
    "io.soabase.halva.implicit.ImplicitContext"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class MasterProcessor extends AbstractProcessor
{
    private static final AliasPassFactory aliasPassFactory = new AliasPassFactory();
    private static final CaseClassPassFactory caseClassPassFactory = new CaseClassPassFactory();
    private static final MonadicForPassFactory monadicForPassFactory = new MonadicForPassFactory();
    private static final ImplicitPassFactory implicitPassFactory = new ImplicitPassFactory();
    private static final Map<String, PassFactory> factories = Map(
        Pair(TypeAlias.class.getName(), aliasPassFactory),
        Pair(CaseClass.class.getName(), caseClassPassFactory),
        Pair(CaseObject.class.getName(), caseClassPassFactory),
        Pair(MonadicFor.class.getName(), monadicForPassFactory),
        Pair(ImplicitClass.class.getName(), implicitPassFactory),
        Pair(ImplicitContext.class.getName(), implicitPassFactory)
    );

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment environment)
    {
        Map<PassFactory, List<WorkItem>> workItems = annotations.stream().flatMap(annotation -> {
            Set<? extends Element> elementsAnnotatedWith = environment.getElementsAnnotatedWith(annotation);
            return elementsAnnotatedWith.stream().map(element -> {
                AnnotationReader annotationReader = new AnnotationReader(processingEnv, element, annotation.getQualifiedName().toString(), annotation.getSimpleName().toString());
                return new WorkItem(element, annotationReader);
            });
        })
        .collect(Collectors.groupingBy(item -> {
            PassFactory passFactory = factories.get(item.getAnnotationReader().getFullName());
            if ( passFactory == null )
            {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Internal error. No factory for " + item.getAnnotationReader().getFullName());
                return (a, b) -> Optional.empty();
            }
            return passFactory;
        }));

        Environment internalEnvironment = makeEnvironment();
        workItems.entrySet().forEach(entry -> {
            PassFactory passFactory = entry.getKey();
            if ( passFactory == null )
            {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Internal error. No factory for " + entry.getKey());
            }
            else
            {
                Optional<Pass> pass = passFactory.firstPass(internalEnvironment, entry.getValue());
                while ( pass.isPresent() )
                {
                    Pass actualPass = pass.get();
                    internalEnvironment.debug(getClass().getSimpleName() + "-" + actualPass.getClass().getSimpleName());
                    pass = actualPass.process();
                }
            }
        });
        return true;
    }

    private Environment makeEnvironment()
    {
        return new Environment()
        {
            @Override
            public void error(Element element, String message)
            {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
            }

            @Override
            public void log(String message)
            {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);
            }

            @Override
            public void debug(String message)
            {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.OTHER, message);
            }

            @Override
            public Elements getElementUtils()
            {
                return processingEnv.getElementUtils();
            }

            @Override
            public Types getTypeUtils()
            {
                return processingEnv.getTypeUtils();
            }

            @Override
            public String getPackage(TypeElement element)
            {
                while ( element.getNestingKind().isNested() )
                {
                    Element enclosingElement = element.getEnclosingElement();
                    if ( enclosingElement instanceof TypeElement )
                    {
                        element = (TypeElement)enclosingElement;
                    }
                    else
                    {
                        break;
                    }
                }
                return element.getEnclosingElement().toString();
            }

            @Override
            public String getGeneratedClassName(TypeElement element, AnnotationReader annotationReader)
            {
                String suffix = annotationReader.getString("suffix");
                String unsuffix = annotationReader.getString("unsuffix");
                String name = element.getSimpleName().toString();
                if ( (unsuffix.length() > 0) && name.endsWith(unsuffix) )
                {
                    return name.substring(0, name.length() - unsuffix.length());
                }
                return name + suffix;
            }

            @Override
            public Collection<Modifier> getModifiers(TypeElement element)
            {
                return element
                    .getModifiers()
                    .stream()
                    .filter(m -> (m != Modifier.ABSTRACT) && (m != Modifier.STATIC))
                    .collect(Collectors.toSet());
            }

            @Override
            public Optional<List<TypeVariableName>> addTypeVariableNames(Consumer<List<TypeVariableName>> applier, List<? extends TypeParameterElement> elements)
            {
                Optional<List<TypeVariableName>> typeVariableNames;
                if ( elements.size() > 0 )
                {
                    List<TypeVariableName> localTypeVariableNames = elements.stream()
                        .map(TypeVariableName::get)
                        .collect(Collectors.toList());
                    applier.accept(localTypeVariableNames);
                    typeVariableNames = Optional.of(localTypeVariableNames);
                }
                else
                {
                    typeVariableNames = Optional.empty();
                }
                return typeVariableNames;
            }

            @Override
            public void createSourceFile(String packageName, ClassName templateQualifiedClassName, ClassName generatedQualifiedClassName, String annotationType, TypeSpec.Builder builder, TypeElement element)
            {
                AnnotationSpec generated = AnnotationSpec
                    .builder(Generated.class)
                    .addMember("value", "\"" + annotationType + "\"")
                    .build();
                builder.addAnnotation(generated);

                TypeSpec classSpec = builder.build();
                JavaFile javaFile = JavaFile.builder(packageName, classSpec)
                    .addFileComment("Auto generated from $L by Soabase " + annotationType + " annotation processor", templateQualifiedClassName)
                    .indent("    ")
                    .build();

                Filer filer = processingEnv.getFiler();
                try
                {
                    JavaFileObject sourceFile = filer.createSourceFile(generatedQualifiedClassName.toString());
                    try ( Writer writer = sourceFile.openWriter() )
                    {
                        javaFile.writeTo(writer);
                    }
                }
                catch ( IOException e )
                {
                    String message = "Could not create source file";
                    if ( e.getMessage() != null )
                    {
                        message = message + ": " + e.getMessage();
                    }
                    error(element, message);
                }
            }

            @Override
            public DeclaredType typeOfFieldOrMethod(Element element)
            {
                if ( element.getKind() == ElementKind.METHOD )
                {
                    return (DeclaredType)((ExecutableElement)element).getReturnType();
                }
                TypeMirror type = element.asType();
                if ( type instanceof DeclaredType )
                {
                    return (DeclaredType)type;
                }
                throw new IllegalArgumentException("Cannot convert to DeclaredType: " + element);
            }

            @Override
            public TypeMirror getResolvedReturnType(ExecutableElement method, DeclaredType enclosing)
            {
                // copied from MethodSpec.override()
                ExecutableType executableType = (ExecutableType)processingEnv.getTypeUtils().asMemberOf(enclosing, method);
                return executableType.getReturnType();
            }
        };
    }
}

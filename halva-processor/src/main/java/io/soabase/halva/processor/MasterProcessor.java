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
package io.soabase.halva.processor;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import io.soabase.halva.alias.TypeAlias;
import io.soabase.halva.caseclass.CaseClass;
import io.soabase.halva.caseclass.CaseObject;
import io.soabase.halva.comprehension.MonadicFor;
import io.soabase.halva.container.TypeContainer;
import io.soabase.halva.implicit.ImplicitClass;
import io.soabase.halva.implicit.ImplicitContext;
import io.soabase.halva.processor.alias.AliasPassFactory;
import io.soabase.halva.processor.caseclass.CaseClassPassFactory;
import io.soabase.halva.processor.comprehension.MonadicForPassFactory;
import io.soabase.halva.processor.container.Container;
import io.soabase.halva.processor.container.ContainerManager;
import io.soabase.halva.processor.container.ContainerPassFactory;
import io.soabase.halva.processor.implicit.ImplicitPassFactory;
import javax.annotation.Generated;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
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
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
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
    "io.soabase.halva.implicit.ImplicitContext",
    "io.soabase.halva.container.TypeContainer"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions({
    "TypeAlias.suffix",
    "TypeAlias.unsuffix",
    "CaseClass.suffix",
    "CaseClass.unsuffix",
    "CaseClass.json",
    "CaseClass.validate",
    "CaseObject.suffix",
    "CaseObject.unsuffix",
    "CaseObject.asEnum",
    "MonadicFor.suffix",
    "MonadicFor.unsuffix",
    "MonadicFor.monadicParameterPosition",
    "MonadicFor.applyParentTypeParameter",
    "ImplicitClass.suffix",
    "ImplicitClass.unsuffix",
    "ImplicitClass.limitContexts",
    "ImplicitClass.excludeContexts",
    "ImplicitContext.limits",
    "ImplicitContext.excludes",
    "TypeContainer.suffix",
    "TypeContainer.unsuffix",
    "TypeContainer.renameContained"
})
public class MasterProcessor extends AbstractProcessor
{
    private static final AliasPassFactory aliasPassFactory = new AliasPassFactory();
    private static final CaseClassPassFactory caseClassPassFactory = new CaseClassPassFactory();
    private static final MonadicForPassFactory monadicForPassFactory = new MonadicForPassFactory();
    private static final ImplicitPassFactory implicitPassFactory = new ImplicitPassFactory();
    private static final ContainerPassFactory containerPassFactory = new ContainerPassFactory();
    private static final Map<String, PassFactory> factories = Map(
        Pair(TypeAlias.class.getName(), aliasPassFactory),
        Pair(CaseClass.class.getName(), caseClassPassFactory),
        Pair(CaseObject.class.getName(), caseClassPassFactory),
        Pair(MonadicFor.class.getName(), monadicForPassFactory),
        Pair(ImplicitClass.class.getName(), implicitPassFactory),
        Pair(ImplicitContext.class.getName(), implicitPassFactory),
        Pair(TypeContainer.class.getName(), containerPassFactory)
    );

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment environment)
    {
        ContainerManager containerManager = new ContainerManager();
        Map<ClassName, ClassName> generatedMap = new HashMap<>();

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
                return new NullPassFactory();
            }
            return passFactory;
        }));

        Environment internalEnvironment = makeEnvironment(containerManager, generatedMap);

        TreeMap<PassFactory, List<WorkItem>> sortedWorkItems = new TreeMap<>(workItems);
        processWorkitems(sortedWorkItems, internalEnvironment);

        buildContainers(containerManager, internalEnvironment);

        return true;
    }

    private void processWorkitems(Map<PassFactory, List<WorkItem>> workItems, Environment internalEnvironment)
    {
        List<Optional<Pass>> secondaryPasses = workItems.entrySet().stream()
            .map(entry -> {
                PassFactory passFactory = entry.getKey();
                if ( passFactory == null )
                {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Internal error. No factory for " + entry.getKey());
                    return Optional.<Pass>empty();
                }

                Optional<Pass> pass = passFactory.firstPass(internalEnvironment, entry.getValue());
                return pass.isPresent() ? pass.get().process() : pass;
            })
            .collect(Collectors.toList());

        secondaryPasses.forEach(pass -> {
            while ( pass.isPresent() )
            {
                pass = pass.get().process();
            }
        });
    }

    private void buildContainers(ContainerManager containerManager, Environment internalEnvironment)
    {
        containerManager.getContainers().forEach(container -> {
            TypeElement typeElement = container.getElement();
            String packageName = internalEnvironment.getPackage(typeElement);
            ClassName templateQualifiedClassName = ClassName.get(packageName, typeElement.getSimpleName().toString());
            ClassName containerQualifiedClassName = ClassName.get(packageName, getDesiredSimpleName(containerManager, typeElement, container.getAnnotationReader()));
            internalCreateSourceFile(packageName, templateQualifiedClassName, containerQualifiedClassName, TypeContainer.class.getName(), container.build(containerQualifiedClassName), typeElement);
        });
    }

    private void internalCreateSourceFile(String packageName, ClassName templateQualifiedClassName, ClassName generatedQualifiedClassName, String annotationType, TypeSpec.Builder builder, TypeElement element)
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
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
        }
    }

    private String getDesiredSimpleName(ContainerManager containerManager, TypeElement element, AnnotationReader annotationReader)
    {
        Optional<Container> container = containerManager.getContainer(element);
        if ( container.isPresent() )
        {
            if ( !container.get().getAnnotationReader().getBoolean("renameContained") )
            {
                return element.getSimpleName().toString();
            }
        }

        String suffix = annotationReader.getString("suffix");
        String unsuffix = annotationReader.getString("unsuffix");
        String name = element.getSimpleName().toString();
        if ( (unsuffix.length() > 0) && name.endsWith(unsuffix) )
        {
            return name.substring(0, name.length() - unsuffix.length());
        }
        return name + suffix;
    }

    private ClassName getQualifiedClassName(ContainerManager containerManager, String packageName, TypeElement element, AnnotationReader annotationReader)
    {
        String generatedClassName = getDesiredSimpleName(containerManager, element, annotationReader);

        Optional<Container> container = containerManager.getContainer(element);
        if ( container.isPresent() )
        {
            String containerName = getDesiredSimpleName(containerManager, container.get().getElement(), container.get().getAnnotationReader());
            return ClassName.get(packageName, containerName, generatedClassName);
        }

        return ClassName.get(packageName, generatedClassName);
    }

    private Environment makeEnvironment(ContainerManager containerManager, Map<ClassName, ClassName> generatedMap)
    {
        return new Environment()
        {
            @Override
            public GeneratedManager getGeneratedManager()
            {
                return new GeneratedManager()
                {
                    @Override
                    public void registerGenerated(TypeElement element, AnnotationReader annotationReader)
                    {
                        ClassName qualifiedClassName = MasterProcessor.this.getQualifiedClassName(containerManager, getPackage(element), element, annotationReader);
                        generatedMap.put(ClassName.get(element), qualifiedClassName);
                    }

                    @Override
                    public TypeName toTypeName(TypeMirror type)
                    {
                        if ( type.getKind() == TypeKind.DECLARED )
                        {
                            Element element = ((DeclaredType)type).asElement();
                            if ( element instanceof TypeElement )
                            {
                                GeneratedClass resolved = internalResolve(ClassName.get((TypeElement)element));
                                if ( resolved.hasGenerated() )
                                {
                                    return resolved.getGenerated();
                                }
                            }
                        }
                        return ClassName.get(type);
                    }

                    @Override
                    public GeneratedClass resolve(TypeElement element)
                    {
                        return internalResolve(ClassName.get(element));
                    }

                    private GeneratedClass internalResolve(ClassName original)
                    {
                        ClassName generated = generatedMap.get(original);
                        return new GeneratedClass(original, generated);
                    }
                };
            }

            @Override
            public ContainerManager getContainerManager()
            {
                return containerManager;
            }

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
                Optional<Container> container = containerManager.getContainer(element);
                if ( container.isPresent() )
                {
                    container.get().addItem(builder);
                }
                else
                {
                    internalCreateSourceFile(packageName, templateQualifiedClassName, generatedQualifiedClassName, annotationType, builder, element);
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
                return ((ExecutableType)getResolvedType(method, enclosing)).getReturnType();
            }

            @Override
            public TypeMirror getResolvedType(Element element, DeclaredType enclosing)
            {
                // copied from MethodSpec.override()
                return processingEnv.getTypeUtils().asMemberOf(enclosing, element);
            }
        };
    }

    private static class NullPassFactory implements PassFactory
    {
        @Override
        public Priority getPriority()
        {
            return Priority.LAST;
        }

        @Override
        public Optional<Pass> firstPass(Environment environment, List<WorkItem> workItems)
        {
            return Optional.empty();
        }
    }
}

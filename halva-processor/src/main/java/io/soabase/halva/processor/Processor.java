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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class Processor extends AbstractProcessor
{
    private final PassFactory passFactory;

    protected Processor(PassFactory passFactory)
    {
        this.passFactory = passFactory;
    }

    @Override
    public final boolean process(Set<? extends TypeElement> annotations, RoundEnvironment environment)
    {
        List<WorkItem> workItems = annotations.stream().flatMap(annotation -> {
            Set<? extends Element> elementsAnnotatedWith = environment.getElementsAnnotatedWith(annotation);
            return elementsAnnotatedWith.stream().map(element -> {
                AnnotationReader annotationReader = new AnnotationReader(processingEnv, element, annotation.getSimpleName().toString());
                return new WorkItem(element, annotationReader);
            });
        }).collect(Collectors.toList());

        Environment internalEnvironment = makeEnvironment();
        Optional<Pass> pass = passFactory.firstPass(internalEnvironment, workItems);
        while ( pass.isPresent() )
        {
            Pass actualPass = pass.get();
            internalEnvironment.debug(getClass().getSimpleName() + "-" + actualPass.getClass().getSimpleName());
            pass = actualPass.process();
        }
        return true;
    }

    public static Optional<List<TypeVariableName>> addTypeVariableNames(Consumer<List<TypeVariableName>> applier, List<? extends TypeParameterElement> elements)
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
                return Processor.addTypeVariableNames(applier, elements);
            }

            @Override
            public void createSourceFile(String packageName, ClassName templateQualifiedClassName, ClassName generatedQualifiedClassName, String annotationType, TypeSpec.Builder builder, TypeElement element)
            {
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
        };
    }
}

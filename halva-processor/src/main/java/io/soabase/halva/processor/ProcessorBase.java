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
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class ProcessorBase<SpecType, TemplatesType> extends AbstractProcessor
{
    private final List<Class<? extends Annotation>> annotations;

    @SafeVarargs
    protected ProcessorBase(Class<? extends Annotation>... annotations)
    {
        this.annotations = Arrays.asList(annotations);
    }

    @Override
    public final boolean process(Set<? extends TypeElement> annotations, RoundEnvironment environment)
    {
        TemplatesType templates = newTemplates();
        this.annotations.stream().forEach(annotation ->
                environment.getElementsAnnotatedWith(annotation).stream()
                    .map(this::getItems)
                    .forEach(spec -> buildClass(templates, spec))
        );
        return true;
    }

    protected abstract void buildClass(TemplatesType templates, SpecType spec);

    protected Collection<Modifier> getModifiers(TypeElement element)
    {
        return element
                .getModifiers()
                .stream()
                .filter(m -> (m != Modifier.ABSTRACT) && (m != Modifier.STATIC))
                .collect(Collectors.toSet());
    }

    protected void createSourceFile(String packageName, ClassName templateQualifiedClassName, ClassName generatedQualifiedClassName, String annotationType, TypeSpec.Builder builder, TypeElement element)
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
            error(element, "Could not create source file", e);
        }
    }

    protected abstract TemplatesType newTemplates();

    protected abstract SpecType getItems(Element element);

    protected void error(Element element, String message, Throwable e)
    {
        if ( (e != null) && (e.getMessage() != null) )
        {
            message = message + ": " + e.getMessage();
        }
        error(element, message);
    }

    protected void error(Element element, String message)
    {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
    }

    protected void log(String message)
    {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, message);
    }

    protected String getPackage(TypeElement element)
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

    protected String getCaseClassSimpleName(TypeElement element, String suffix, String unsuffix)
    {
        String name = element.getSimpleName().toString();
        if ( (unsuffix.length() > 0) && name.endsWith(unsuffix) )
        {
            return name.substring(0, name.length() - unsuffix.length());
        }
        return name + suffix;
    }
}

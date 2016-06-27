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

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AnnotationReader
{
    private final String annotationName;
    private final Map<? extends ExecutableElement, ? extends AnnotationValue> values;
    private final String annotationFullName;

    AnnotationReader(ProcessingEnvironment processingEnv, Element element, String annotationFullName, String annotationName)
    {
        this.annotationFullName = annotationFullName;
        Optional<? extends AnnotationMirror> annotation = (element == null) ? Optional.empty() :
            element.getAnnotationMirrors().stream()
            .filter(mirror -> element.getAnnotationMirrors().get(0).getAnnotationType().asElement().getSimpleName().toString().equals(annotationName))
            .findFirst();
        if ( !annotation.isPresent() )
        {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Internal error. Could not find annotation: " + annotationName, element);
        }
        this.annotationName = annotationName;
        values = annotation.isPresent() ? processingEnv.getElementUtils().getElementValuesWithDefaults(annotation.get()) : null;
    }

    public String getName()
    {
        return annotationName;
    }

    public String getFullName()
    {
        return annotationFullName;
    }

    public boolean getBoolean(String named)
    {
        Optional<? extends AnnotationValue> found = find(named);
        if ( found.isPresent() )
        {
            return ((Boolean)found.get().getValue());
        }
        return false;
    }

    public int getInt(String named)
    {
        Optional<? extends AnnotationValue> found = find(named);
        if ( found.isPresent() )
        {
            return ((Integer)found.get().getValue());
        }
        return 0;
    }

    public String getString(String named)
    {
        Optional<? extends AnnotationValue> found = find(named);
        if ( found.isPresent() )
        {
            return String.valueOf(found.get().getValue());
        }
        return "";
    }

    @SuppressWarnings("unchecked")
    public List<TypeMirror> getClasses(String named)
    {
        Optional<? extends AnnotationValue> found = find(named);
        if ( found.isPresent() )
        {
            List<? extends AnnotationValue> values = (List<? extends AnnotationValue>)found.get().getValue();
            return values.stream().map(v -> (TypeMirror)v.getValue()).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private Optional<? extends AnnotationValue> find(String named)
    {
        if ( values == null )
        {
            return Optional.empty();
        }
        return values.entrySet().stream()
            .filter(entry -> entry.getKey().getSimpleName().toString().equals(named))
            .map(Map.Entry::getValue)
            .findFirst();
    }
}

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AnnotationReader
{
    private final String annotationName;
    private final Map<String, Object> values;
    private final ProcessingEnvironment processingEnv;
    private final String annotationFullName;

    AnnotationReader(ProcessingEnvironment processingEnv, Element element, String annotationFullName, String annotationName)
    {
        this.processingEnv = processingEnv;
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
        values = new HashMap<>();
        if ( annotation.isPresent() )
        {
            Map<? extends ExecutableElement, ? extends AnnotationValue> specifiedValues = annotation.get().getElementValues();
            Map<? extends ExecutableElement, ? extends AnnotationValue> valuesWithDefaults = processingEnv.getElementUtils().getElementValuesWithDefaults(annotation.get());
            valuesWithDefaults.entrySet().forEach(entry -> {
                ExecutableElement key = entry.getKey();
                String overrideKey = annotationName + "." + key.getSimpleName().toString();
                if ( specifiedValues.containsKey(entry.getKey()) )
                {
                    values.put(key.getSimpleName().toString(), specifiedValues.get(key).getValue());
                }
                else if ( processingEnv.getOptions().containsKey(overrideKey) )
                {
                    values.put(key.getSimpleName().toString(), processingEnv.getOptions().get(overrideKey));
                }
                else
                {
                    values.put(key.getSimpleName().toString(), entry.getValue().getValue());
                }
            });
        }
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
        Object value = values.get(named);
        //noinspection SimplifiableIfStatement
        if ( value != null )
        {
            return (value instanceof Boolean) ? ((Boolean)value).booleanValue() : Boolean.valueOf(String.valueOf(value));
        }
        return false;
    }

    public int getInt(String named)
    {
        Object value = values.get(named);
        if ( value != null )
        {
            try
            {
                return (value instanceof Integer) ? ((Integer)value) : Integer.parseInt(String.valueOf(value));
            }
            catch ( NumberFormatException ignore )
            {
                // ignore
            }
        }
        return 0;
    }

    public String getString(String named)
    {
        Object value = values.getOrDefault(named, "");
        return String.valueOf(value);
    }

    @SuppressWarnings("unchecked")
    public List<TypeMirror> getClasses(String named)
    {
        Object value = values.get(named);
        if ( value != null )
        {
            if ( value instanceof List )
            {
                List<? extends AnnotationValue> values = (List<? extends AnnotationValue>)value;
                return values.stream().map(v -> (TypeMirror)v.getValue()).collect(Collectors.toList());
            }

            return Arrays.asList(String.valueOf(value).split(",")).stream()
                .filter(s -> s.trim().length() > 0)
                .map(s -> processingEnv.getElementUtils().getTypeElement(s.trim()))
                .filter(e -> e != null)
                .map(Element::asType)
                .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}

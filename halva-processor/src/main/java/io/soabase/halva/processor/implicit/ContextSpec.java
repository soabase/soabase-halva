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
package io.soabase.halva.processor.implicit;

import io.soabase.halva.processor.AnnotationReader;
import io.soabase.halva.processor.SpecBase;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ContextSpec implements SpecBase
{
    private final TypeElement typeElement;
    private final List<ContextItem> items;
    private final AnnotationReader annotationReader;

    ContextSpec()
    {
        this(null, null, new ArrayList<>());
    }

    ContextSpec(TypeElement typeElement, AnnotationReader annotationReader, List<ContextItem> items)
    {
        this.annotationReader = annotationReader;
        this.typeElement = typeElement;
        this.items = Collections.unmodifiableList(new ArrayList<>(items));
    }

    @Override
    public TypeElement getAnnotatedElement()
    {
        return typeElement;
    }

    AnnotationReader getAnnotationReader()
    {
        return annotationReader;
    }

    List<ContextItem> getItems()
    {
        return items;
    }

    boolean isValid()
    {
        return (typeElement != null);
    }
}

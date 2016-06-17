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
package io.soabase.halva.processor.implicit2;

import io.soabase.halva.processor.AnnotationReader;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ImplicitSpec
{
    private final TypeElement typeElement;
    private final AnnotationReader annotationReader;
    private final List<ImplicitItem> items;

    ImplicitSpec(TypeElement typeElement, AnnotationReader annotationReader, List<ImplicitItem> items)
    {
        this.typeElement = typeElement;
        this.annotationReader = annotationReader;
        this.items = Collections.unmodifiableList(new ArrayList<>(items));
    }

    AnnotationReader getAnnotationReader()
    {
        return annotationReader;
    }

    List<ImplicitItem> getItems()
    {
        return items;
    }

    TypeElement getAnnotatedElement()
    {
        return typeElement;
    }
}

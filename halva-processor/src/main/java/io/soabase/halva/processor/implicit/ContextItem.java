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
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import java.util.concurrent.atomic.AtomicBoolean;

class ContextItem
{
    private final TypeElement classElement;
    private final AnnotationReader annotationReader;
    private final Element element;
    private final DeclaredType elementType;
    private final AtomicBoolean mapClassBuilt = new AtomicBoolean(false);

    ContextItem(TypeElement classElement, AnnotationReader annotationReader, Element element, DeclaredType elementType)
    {
        this.classElement = classElement;
        this.annotationReader = annotationReader;
        this.element = element;
        this.elementType = elementType;
    }

    TypeElement getClassElement()
    {
        return classElement;
    }

    AnnotationReader getAnnotationReader()
    {
        return annotationReader;
    }

    Element getElement()
    {
        return element;
    }

    DeclaredType getElementType()
    {
        return elementType;
    }

    boolean mapClassNeedsBuilding()
    {
        return mapClassBuilt.compareAndSet(false, true);
    }
}

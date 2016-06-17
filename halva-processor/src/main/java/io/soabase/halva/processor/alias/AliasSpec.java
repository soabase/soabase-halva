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
package io.soabase.halva.processor.alias;

import io.soabase.halva.processor.AnnotationReader;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

class AliasSpec
{
    private final TypeElement typeElement;
    private final AnnotationReader annotationReader;
    private final DeclaredType parameterizedType;

    AliasSpec(TypeElement typeElement, AnnotationReader annotationReader, DeclaredType parameterizedType)
    {
        this.typeElement = typeElement;
        this.annotationReader = annotationReader;
        this.parameterizedType = parameterizedType;
    }

    TypeElement getAnnotatedElement()
    {
        return typeElement;
    }

    AnnotationReader getAnnotationReader()
    {
        return annotationReader;
    }

    DeclaredType getParameterizedType()
    {
        return parameterizedType;
    }
}

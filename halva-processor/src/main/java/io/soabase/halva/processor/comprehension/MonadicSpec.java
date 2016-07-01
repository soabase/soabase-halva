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
package io.soabase.halva.processor.comprehension;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import io.soabase.halva.processor.AnnotationReader;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

class MonadicSpec
{
    private final TypeElement factoryElement;
    private final MonadType monadType;
    private final AnnotationReader annotationReader;

    MonadicSpec(TypeElement factoryElement, MonadType monadType, AnnotationReader annotationReader)
    {
        this.factoryElement = factoryElement;
        this.monadType = monadType;
        this.annotationReader = annotationReader;
    }

    TypeElement getAnnotatedElement()
    {
        return factoryElement;
    }

    AnnotationReader getAnnotationReader()
    {
        return annotationReader;
    }

    TypeElement getMonadElement()
    {
        return monadType.getElement();
    }

    DeclaredType getMonadType()
    {
        return monadType.getType();
    }
}

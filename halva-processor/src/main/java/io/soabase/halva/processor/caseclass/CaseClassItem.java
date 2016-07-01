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
package io.soabase.halva.processor.caseclass;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

class CaseClassItem
{
    private final String name;
    private final ExecutableElement element;
    private final TypeMirror type;
    private final boolean hasDefaultValue;
    private final boolean mutable;

    CaseClassItem(String name, ExecutableElement element, TypeMirror type, boolean hasDefaultValue, boolean mutable)
    {
        this.name = name;
        this.element = element;
        this.type = type;
        this.hasDefaultValue = hasDefaultValue;
        this.mutable = mutable;

        if ( type.getKind() == TypeKind.WILDCARD )
        {
            System.out.println();
        }
    }

    ExecutableElement getElement()
    {
        return element;
    }

    String getName()
    {
        return name;
    }

    TypeMirror getType()
    {
        return type;
    }

    boolean hasDefaultValue()
    {
        return hasDefaultValue;
    }

    boolean isMutable()
    {
        return mutable;
    }

    @Override
    public String toString()
    {
        return "CaseClassItem{" +
            "name=" + name +
            ", type=" + type +
            ", hasDefaultValue=" + hasDefaultValue +
            ", mutable=" + mutable +
            '}';
    }
}

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
package io.soabase.halva.alias.details;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;

class AliasSpec
{
    private final TypeElement typeElement;
    private final DeclaredType parameterizedType;

    AliasSpec()
    {
        this(null, null);
    }

    AliasSpec(TypeElement typeElement, DeclaredType parameterizedType)
    {

        this.typeElement = typeElement;
        this.parameterizedType = parameterizedType;
    }

    boolean isValid()
    {
        return typeElement != null;
    }

    TypeElement getElement()
    {
        return typeElement;
    }

    DeclaredType getParameterizedType()
    {
        return parameterizedType;
    }
}

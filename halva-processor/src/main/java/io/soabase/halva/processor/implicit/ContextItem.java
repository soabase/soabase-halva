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

import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;

class ContextItem
{
    private final Element element;
    private final DeclaredType type;
    private final boolean isSpecificTypesMapMatch;

    ContextItem()
    {
        this(null, null, false);
    }

    ContextItem(Element element, DeclaredType type)
    {
        this(element, type, false);
    }

    ContextItem(Element element, DeclaredType type, boolean isSpecificTypesMapMatch)
    {
        this.element = element;
        this.type = type;
        this.isSpecificTypesMapMatch = isSpecificTypesMapMatch;
    }

    DeclaredType getType()
    {
        return type;
    }

    Element getElement()
    {
        return element;
    }

    boolean isValid()
    {
        return (element != null);
    }

    boolean isSpecificTypesMapMatch()
    {
        return isSpecificTypesMapMatch;
    }
}

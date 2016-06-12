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

import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class CaseClassSpec
{
    private final Optional<TypeElement> element;
    private final List<CaseClassItem> items;

    CaseClassSpec()
    {
        element = Optional.empty();
        items = new ArrayList<>();
    }

    CaseClassSpec(TypeElement element, List<CaseClassItem> items)
    {
        this.element = Optional.of(element);
        this.items = items;
    }

    TypeElement getElement()
    {
        return element.orElseThrow(() -> new RuntimeException("Empty spec being used"));
    }

    List<CaseClassItem> getItems()
    {
        return items;
    }
}

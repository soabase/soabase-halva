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

import io.soabase.halva.processor.Environment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

class ImplicitSearcher
{
    private final Environment environment;
    private final List<ContextItem> contextItems;

    ImplicitSearcher(Environment environment, List<ContextItem> contextItems)
    {
        this.environment = environment;
        this.contextItems = contextItems;
    }

    FoundImplicit find(TypeMirror type)
    {
        List<FoundImplicit> foundImplicits = new ArrayList<>();
        contextItems.forEach(item -> {
            Element element = item.getElement();
            TypeMirror compareType = environment.typeOfFieldOrMethod(element);
            if ( environment.getTypeUtils().isAssignable(compareType, type) )
            {
                foundImplicits.add(new FoundImplicit(element));
            }
        });

        if ( foundImplicits.size() != 1 )
        {
            String message = (foundImplicits.size() == 0) ? "No matches found for implicit for " : "Multiple matches found for implicit for ";
            environment.error(environment.getTypeUtils().asElement(type), message + type);
            return null;
        }

        return foundImplicits.get(0);
    }
}

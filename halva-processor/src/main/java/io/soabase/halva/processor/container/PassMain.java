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
package io.soabase.halva.processor.container;

import io.soabase.halva.processor.Environment;
import io.soabase.halva.processor.Pass;
import io.soabase.halva.processor.WorkItem;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Optional;

class PassMain implements Pass
{
    private final Environment environment;
    private final List<WorkItem> workItems;

    PassMain(Environment environment, List<WorkItem> workItems)
    {
        this.environment = environment;
        this.workItems = workItems;
    }

    @Override
    public Optional<Pass> process()
    {
        workItems.forEach(this::processItem);
        return Optional.empty();
    }

    private void processItem(WorkItem item)
    {
        if ( item.getElement().getKind() != ElementKind.INTERFACE )
        {
            environment.error(item.getElement(), "@TypeContainer can only be used with interfaces");
        }
        else
        {
            environment.getContainerManager().addContainer(new Container(environment, (TypeElement)item.getElement(), item.getAnnotationReader()));
        }
    }
}

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
import io.soabase.halva.processor.PassFactory;
import io.soabase.halva.processor.WorkItem;
import java.util.List;
import java.util.Optional;

public class ContainerPassFactory implements PassFactory
{
    @Override
    public Priority getPriority()
    {
        return Priority.FIRST;
    }

    @Override
    public Optional<Pass> firstPass(Environment environment, List<WorkItem> workItems)
    {
        return Optional.of(new PassMain(environment, workItems));
    }
}

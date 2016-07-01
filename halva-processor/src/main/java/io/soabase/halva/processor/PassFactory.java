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
package io.soabase.halva.processor;

import java.util.List;
import java.util.Optional;

public interface PassFactory extends Comparable<PassFactory>
{
    enum Priority
    {
        FIRST(0),
        SECOND(1),
        THIRD(2),
        FOURTH(3),
        LAST(4)
        ;

        public int getValue()
        {
            return value;
        }

        private final int value;

        Priority(int value)
        {
            this.value = value;
        }
    }

    Priority getPriority();

    Optional<Pass> firstPass(Environment environment, List<WorkItem> workItems);

    @Override
    default int compareTo(PassFactory rhs)
    {
        if ( rhs == null )
        {
            return -1;
        }
        int diff = getPriority().getValue() - rhs.getPriority().getValue();
        if ( diff == 0 )
        {
            diff = hashCode() - rhs.hashCode();
        }
        return (diff < 0) ? -1 : ((diff > 0) ? 1 : 0);
    }
}

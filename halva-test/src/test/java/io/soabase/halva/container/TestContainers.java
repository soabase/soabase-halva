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
package io.soabase.halva.container;

import io.soabase.halva.alias.TypeAlias;
import io.soabase.halva.caseclass.CaseClass;
import io.soabase.halva.sugar.ConsList;
import java.util.List;

public class TestContainers
{
    @TypeContainer
    interface TestContainer_
    {
        @TypeAlias interface Stack extends ConsList<List<String>>{}

        @CaseClass interface MyStack{Stack stack(); int value();}
    }
}

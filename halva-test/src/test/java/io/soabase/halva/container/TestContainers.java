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
import org.junit.Assert;
import org.junit.Test;
import java.util.List;

import static io.soabase.halva.container.TestContainer.Stack.Stack;
import static io.soabase.halva.sugar.Sugar.List;

public class TestContainers
{
    @TypeContainer
    interface TestContainer_
    {
        @TypeAlias interface Stack extends ConsList<List<String>>{}

        @CaseClass interface MyStack{TestContainer.Stack stack(); int value();}
    }

    @Test
    public void testBasic()
    {
        TestContainer.Stack stack = Stack(List(List("one", "two", "three"), List("four", "five")));
        TestContainer.MyStack myStack = TestContainer.MyStack.MyStack(stack, 10);
        Assert.assertEquals(10, myStack.value());
        Assert.assertEquals(2, myStack.stack().size());
    }
}

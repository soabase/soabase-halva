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
package io.soabase.halva.caseclass;

import io.soabase.halva.caseclass.sub1.ChildCase;
import io.soabase.halva.caseclass.sub2.ParentCase;
import org.junit.Assert;
import org.junit.Test;

import static io.soabase.halva.caseclass.sub1.ChildCase.ChildCase;
import static io.soabase.halva.caseclass.sub2.ParentCase.ParentCase;

public class TestCrossReference
{
    @Test
    public void testParentChild()
    {
        ParentCase parent = ParentCase(10, 20);
        ChildCase child = ChildCase("test", parent);
        Assert.assertEquals(parent, child.parent());
    }
}

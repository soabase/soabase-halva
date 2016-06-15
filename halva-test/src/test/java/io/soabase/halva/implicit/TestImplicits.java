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
package io.soabase.halva.implicit;

import org.junit.Assert;
import org.junit.Test;

public class TestImplicits
{
    @Test
    public void testInjection()
    {
        AnimalCage animalCage = new AnimalCageImpl();
        Assert.assertEquals("dog", animalCage.getAnimal().type());
        Assert.assertEquals(4, animalCage.getAnimal().numberOfLegs());
    }

    @Test
    public void testPimped()
    {
        //noinspection MismatchedQueryAndUpdateOfCollection
        BaseClassImpl base = new BaseClassImpl();
        Assert.assertEquals("n = 10", base.getExtraString(10));
        Assert.assertEquals(System.currentTimeMillis() / 10000, base.getTime() / 10000);
    }
}

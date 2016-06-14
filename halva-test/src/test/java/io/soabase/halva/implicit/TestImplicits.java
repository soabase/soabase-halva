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

import io.soabase.halva.any.AnyDeclaration;
import org.junit.Assert;
import org.junit.Test;

import static io.soabase.halva.implicit.FeedCase.FeedCase;
import static io.soabase.halva.implicit.Implicits.Implicits;

public class TestImplicits
{
    public static class Dog extends AnimalCase
    {
        public Dog()
        {
            super("Dog", 4);
        }
    }

    public static class Spider extends AnimalCase
    {
        public Spider()
        {
            super("Spider", 8);
        }
    }

    @Test
    public void testInjection()
    {
        Implicits().setValue(AnyDeclaration.of(Feed.class), FeedCase("rice"));
        Implicits().setValue(AnyDeclaration.of(Animal.class), new Dog());
        Implicits().setValue(AnyDeclaration.of(AnimalCage.class), new AnimalCageImpl());
        StoreShelfImpl shelf = new StoreShelfImpl();
        Assert.assertEquals("Dog", shelf.getCage().getAnimal().type());
        Assert.assertEquals("rice", shelf.getFeed().type());
    }
}

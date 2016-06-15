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

import io.soabase.halva.any.AnyType;
import io.soabase.halva.sugar.ConsList;
import org.junit.Assert;
import org.junit.Test;

import static io.soabase.halva.sugar.Sugar.List;

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
        Assert.assertEquals("dog: 10", base.getExtraString(10));
        Assert.assertEquals(System.currentTimeMillis() / 10000, base.getTime() / 10000);
    }

    // from http://docs.scala-lang.org/tutorials/tour/implicit-parameters.html

    interface SemiGroup<A>
    {
        A add(A x, A y);
    }

    interface Monoid<A> extends SemiGroup<A>
    {
        A unit();
    }

    @ImplicitContext
    public static class ImplicitTestContext
    {
        @Implicit public static final Monoid<String> stringMonoid = new Monoid<String>()
        {
            @Override
            public String unit()
            {
                return "";
            }

            @Override
            public String add(String x, String y)
            {
                return x.toUpperCase() + y.toUpperCase();
            }
        };

        @Implicit public static final Monoid<Integer> intMonoid = new Monoid<Integer>()
        {
            @Override
            public Integer unit()
            {
                return 0;
            }

            @Override
            public Integer add(Integer x, Integer y)
            {
                return x + y;
            }
        };
    }

    @ImplicitClass
    public static class Sum
    {
        public <A> A sum(ConsList<A> xs, @Implicit Monoid<A> m)
        {
            if ( xs.size() == 0 )
            {
                return m.unit();
            }
            return m.add(xs.head(), sum(xs.tail(), m));
        }
    }

    @Test
    public void testImplicitScalaLangTutorial()
    {
        Assert.assertEquals(new Integer(6), new SumImpl().sum(List(1, 2, 3), new AnyType<Monoid<Integer>>(){}));
        Assert.assertEquals("ABC", new SumImpl().sum(List("a", "b", "c"), new AnyType<Monoid<String>>(){}));
    }
}

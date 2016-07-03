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
package io.soabase.halva.any;

import io.soabase.halva.alias.TypeAlias;
import io.soabase.halva.sugar.ConsList;
import io.soabase.halva.tuple.Pair;
import io.soabase.halva.tuple.Tuple;
import org.junit.Assert;
import org.junit.Test;

import static io.soabase.halva.sugar.Sugar.List;
import static io.soabase.halva.tuple.Tuple.Pair;
import static org.junit.Assert.assertEquals;

public class TestAny
{
    @Test
    public void testAny()
    {
        Any<Object> any = new AnyType<Object>(){};
        assertEquals(Tuple.Tu("a", any, "b"), Tuple.Tu("a", Tuple.Tu(1, 2, 3), "b"));
        assertEquals(Tuple.Tu("a", Tuple.Tu(1, 2, 3), "b"), Tuple.Tu("a", any, "b"));
        assertEquals(Tuple.Tu("a", any, "b"), Tuple.Tu("a", any, "b"));
    }

    @TypeAlias public interface Environment_ extends ConsList<Pair<String, Integer>>{}

    @Test
    public void testAliasedCons()
    {
        Any<String> s = new AnyType<String>(){};
        Any<Integer> v = new AnyType<Integer>(){};
        Any<Environment> e = Any.typeAlias(Environment.TypeAliasType);
        AnyList cons = Any.headAnyTail(Pair(s, v), e);

        Assert.assertTrue(cons.canSet(List(Pair("10", 10), Pair("20", 20), Pair("30", 30))));
    }
}

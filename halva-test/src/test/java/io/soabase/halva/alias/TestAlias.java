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
package io.soabase.halva.alias;

import io.soabase.halva.sugar.ConsList;
import org.junit.Assert;
import org.junit.Test;
import java.util.List;
import java.util.Map;

import static io.soabase.halva.alias.ConcreteTypeAlias.ConcreteTypeAlias;
import static io.soabase.halva.alias.StringList.StringList;
import static io.soabase.halva.sugar.Sugar.List;
import static io.soabase.halva.sugar.Sugar.Map;
import static io.soabase.halva.tuple.Tuple.Pair;

public class TestAlias
{
    @TypeAlias interface StringList_ extends ConsList<String>{}

    @TypeAlias class ConcreteTypeAlias_ extends ConcreteType<List<String>, Map<String, String>>{}

    @Test
    public void testBasic()
    {
        StringList s = StringList(List("1", "2", "3"));
        Assert.assertEquals(List("1", "2", "3").size(), s.size());
        Assert.assertEquals(List("1", "2", "3"), s);
    }

    @Test
    public void testConcreteType()
    {
        ConcreteType<List<String>, Map<String, String>> instance = new ConcreteType<>(List("1", "2"), Map(Pair("a", "b")));
        ConcreteTypeAlias c = ConcreteTypeAlias(instance);
        c.setA(List("one", "two"));
        c.setB(Map(Pair("1", "2"), Pair("3", "4")));
        Assert.assertEquals(c.getA(), List("one", "two"));
        Assert.assertEquals(c.getB(), Map(Pair("1", "2"), Pair("3", "4")));
    }
}

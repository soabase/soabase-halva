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
package io.soabase.halva.suagar;

import org.junit.Assert;
import org.junit.Test;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.soabase.halva.suagar.Sugar.*;
import static io.soabase.halva.tuple.Tuple.T;

public class TestSugarObjects
{
    @Test
    public void testList()
    {
        List<String> empty = List();
        Assert.assertNotNull(empty);
        Assert.assertEquals(0, empty.size());

        Assert.assertEquals(Arrays.asList("10", "20", "30"), List("10", "20", "30"));

        ConsList<String> l = List("10", "20", "30");

        Assert.assertEquals("10", l.head());
        Assert.assertEquals(List("20", "30"), l.tail());

        Assert.assertEquals(List("0", "10", "20", "30"), cons("0", l));
        Assert.assertEquals(List("1", "2", "3", "10", "20", "30"), concat(List("1", "2", "3"), l));
    }

    @Test
    public void testSet()
    {
        Set<String> empty = Set();
        Assert.assertNotNull(empty);
        Assert.assertEquals(0, empty.size());

        Set<String> expected = new HashSet<>();
        expected.addAll(Arrays.asList("10", "20", "30"));
        Assert.assertEquals(expected, Set("10", "20", "30"));
    }

    @Test
    public void testMap()
    {
        Map<String, Integer> empty = Map();
        Assert.assertNotNull(empty);
        Assert.assertEquals(0, empty.size());

        Map<String, Integer> expected = new HashMap<>();
        expected.put("10", 10);
        expected.put("20", 20);
        expected.put("30", 30);
        Assert.assertEquals(expected, Map(T("10", 10), T("20", 20), T("30", 30)));
    }
}

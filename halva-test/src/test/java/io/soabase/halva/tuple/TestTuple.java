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
package io.soabase.halva.tuple;

import io.soabase.halva.any.Any;
import io.soabase.halva.any.AnyList;
import io.soabase.halva.any.AnyVal;
import io.soabase.halva.sugar.ConsList;
import org.junit.Assert;
import org.junit.Test;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static io.soabase.halva.matcher.Matcher.match;
import static io.soabase.halva.sugar.Sugar.List;
import static io.soabase.halva.tuple.Tuple.Pair;
import static io.soabase.halva.tuple.Tuple.Tu;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
public class TestTuple
{
    @Test
    public void testBasic()
    {
        assertEquals(Tu(), Tu());
        assertEquals(Tu("x"), Tu("x"));
        assertNotEquals("x", Tu("x"));
        assertEquals(Tu("x"), "x");

        assertEquals(Tu("x", 10, Charset.defaultCharset()), Tu("x", 10, Charset.defaultCharset()));

        List<String> arrayList = Arrays.asList("a", "b", "c");
        Collection<String> set = arrayList.stream().collect(Collectors.toSet());
        assertNotEquals(Tu(arrayList), Tu(set));

        assertEquals(Tu("a", Tu(1, 2, 3), "b"), Tu("a", Tu(1, 2, 3), "b"));
    }

    private String extractFunc(ConsList<Pair<String, Integer>> list)
    {
        Any<Pair<String, Integer>> p = new AnyVal<Pair<String, Integer>>(){};
        Any<ConsList<? extends Pair<String, Integer>>> t = new AnyVal<ConsList<? extends Pair<String, Integer>>>(){};

        AnyList a = Any.headAnyTail(Pair("10", 10), t);
        AnyList b = Any.headTail(Pair("-10", -10), List(Pair("20", 20)));
        AnyList c = Any.anyHeadTail(p, List(Pair("20", 20), Pair("30", 30)));
        AnyList d = Any.anyHeadAnyTail(p, t);

        return match(list)
            .caseOf(List(), () -> "empty")
            .caseOf(a, () -> "10/10 :: " + t.val())
            .caseOf(b, () -> "-10/-10 :: 20/20")
            .caseOf(c, () -> p.val() + " :: 20/20 30/30")
            .caseOf(d, () -> p.val() + " :: " + t.val())
            .caseOf(() -> "error")
            .get();
    }

    @Test
    public void testConsListExtraction()
    {
        Assert.assertEquals("empty", extractFunc(List()));
        Assert.assertEquals("10/10 :: " + List(Pair("100", 100), Pair("200", 200)), extractFunc(List(Pair("10", 10), Pair("100", 100), Pair("200", 200))));
        Assert.assertEquals("-10/-10 :: 20/20", extractFunc(List(Pair("-10", -10), Pair("20", 20))));
        Assert.assertEquals(Pair("100", 100) + " :: 20/20 30/30", extractFunc(List(Pair("100", 100), Pair("20", 20), Pair("30", 30))));
        Assert.assertEquals(Pair("66", 66) + " :: " + List(Pair("100", 100), Pair("200", 200)), extractFunc(List(Pair("66", 66), Pair("100", 100), Pair("200", 200))));
    }
}

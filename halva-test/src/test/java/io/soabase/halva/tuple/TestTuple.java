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
import io.soabase.halva.any.AnyDeclaration;
import io.soabase.halva.any.AnyType;
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
import static io.soabase.halva.tuple.Tuple.T;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
public class TestTuple
{
    @Test
    public void testBasic()
    {
        assertEquals(T(), T());
        assertEquals(T("x"), T("x"));
        assertNotEquals("x", T("x"));
        assertEquals(T("x"), "x");

        assertEquals(T("x", 10, Charset.defaultCharset()), T("x", 10, Charset.defaultCharset()));

        List<String> arrayList = Arrays.asList("a", "b", "c");
        Collection<String> set = arrayList.stream().collect(Collectors.toSet());
        assertNotEquals(T(arrayList), T(set));

        assertEquals(T("a", T(1, 2, 3), "b"), T("a", T(1, 2, 3), "b"));
    }

    private String cons(ConsList<Pair<String, Integer>> list)
    {
        Any<Pair<String, Integer>> p = AnyDeclaration.of(new AnyType<Pair<String, Integer>>(){}).define();
        Any<ConsList<Pair<String, Integer>>> t = AnyDeclaration.of(new AnyType<ConsList<Pair<String, Integer>>>(){}).define();

        Any<Void> a = Any.defineHeadAnyTail(Pair("10", 10), t);
        Any<Void> b = Any.defineHeadTail(Pair("-10", -10), List(Pair("20", 20)));
        Any<Void> c = Any.defineAnyHeadTail(p, List(Pair("20", 20), Pair("30", 30)));
        Any<Void> d = Any.defineAnyHeadAnyTail(p, t);

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
        Assert.assertEquals("empty", cons(List()));
        Assert.assertEquals("10/10 :: " + List(Pair("100", 100), Pair("200", 200)), cons(List(Pair("10", 10), Pair("100", 100), Pair("200", 200))));
        Assert.assertEquals("-10/-10 :: 20/20", cons(List(Pair("-10", -10), Pair("20", 20))));
        Assert.assertEquals(Pair("100", 100) + " :: 20/20 30/30", cons(List(Pair("100", 100), Pair("20", 20), Pair("30", 30))));
        Assert.assertEquals(Pair("66", 66) + " :: " + List(Pair("100", 100), Pair("200", 200)), cons(List(Pair("66", 66), Pair("100", 100), Pair("200", 200))));
    }
}

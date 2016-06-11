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

import com.company.GenericExampleCase;
import io.soabase.halva.any.Any;
import io.soabase.halva.any.AnyDeclaration;
import io.soabase.halva.any.AnyType;
import io.soabase.halva.sugar.ConsList;
import io.soabase.halva.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import java.util.List;

import static com.company.GenericExampleCase.GenericExampleCase;
import static io.soabase.halva.any.AnyDeclaration.anyInt;
import static io.soabase.halva.caseclass.Value.Value;
import static io.soabase.halva.caseclass.Value.ValueT;
import static io.soabase.halva.comprehension.For.For;
import static io.soabase.halva.matcher.Matcher.match;
import static io.soabase.halva.sugar.Sugar.List;
import static io.soabase.halva.tuple.Tuple.Pair;
import static io.soabase.halva.tuple.Tuple.T;

public class TestMatcher
{
    @Test
    public void testBasic()
    {
        Any<Integer> anyInt = AnyDeclaration.of(Integer.class).define();

        GenericExampleCase<String, Integer> generic = GenericExampleCase("hey", 100);
        int value = match(generic)
            .caseOf(T("hey", anyInt), anyInt::val)
            .caseOf(() -> 0)
            .get();
        Assert.assertEquals(100, value);

        String s = match(generic)
            .caseOf(T("hey", anyInt), () -> anyInt.val() > 100, () -> "too big")
            .caseOf(T("hey", anyInt), () -> "It's " + anyInt.val())
            .get();
        Assert.assertEquals("It's 100", s);
    }

    @CaseClass interface Value_{int n();};

    int subtract(Value a, Value b)
    {
        Any<Integer> m = anyInt().define();
        Any<Integer> n = anyInt().define();

        return match(Pair(a, b))
            .caseOf( Pair(ValueT(m), ValueT(n)), () -> m.val() - n.val())
            .caseOf( () -> 0)
            .get();
    }

    @Test
    public void testMatchAllPartsOfPair()
    {
        Assert.assertEquals(3, subtract(Value(6), Value(3)));
        Assert.assertEquals(-3, subtract(Value(3), Value(6)));
    }

    static AnyDeclaration<Pair<String, Integer>> myDecl = AnyDeclaration.of(new AnyType<Pair<String, Integer>>(){});

    static List<Pair<String, Integer>> findMatches(String key, ConsList<Pair<String, Integer>> list) {
        Any<Pair<String, Integer>> foundPair = myDecl.define();

        return For(foundPair, list)
            .when(() -> foundPair.val()._1.equals(key))
            .yield(foundPair::val);
    }

    @Test
    public void testFindMatches()
    {
        Assert.assertEquals(List(Pair("even", 2), Pair("even", 4)), findMatches("even", List(Pair("odd", 1), Pair("even", 2), Pair("odd", 3), Pair("even", 4))));
    }

    @Test
    public void testListExtraction()
    {
        ConsList<Pair<String, Integer>> list = List(Pair("even", 2), Pair("even", 4));
        Any<Pair<String, Integer>> anyStringIntPair = AnyDeclaration.of(new AnyType<Pair<String, Integer>>(){}).define();
        Any<ConsList<Pair<String, Integer>>> anyPairList = AnyDeclaration.of(new AnyType<ConsList<Pair<String, Integer>>>(){}).define();

        Any<Void> patternMatcher = Any.defineAnyHeadAnyTail(anyStringIntPair, anyPairList);
        String str = match(list)
            .caseOf(patternMatcher, () -> "The tail is: " + anyPairList.val())
            .get();
        Assert.assertEquals("The tail is: " + list.tail(), str);
    }
}

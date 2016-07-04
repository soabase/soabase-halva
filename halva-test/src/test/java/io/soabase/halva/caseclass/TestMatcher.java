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
import io.soabase.halva.any.AnyType;
import io.soabase.halva.any.AnyVal;
import io.soabase.halva.matcher.MatchError;
import io.soabase.halva.matcher.Matcher;
import io.soabase.halva.matcher.Partial;
import io.soabase.halva.sugar.ConsList;
import io.soabase.halva.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import java.util.List;
import java.util.Optional;

import static com.company.GenericExampleCase.GenericExampleCase;
import static io.soabase.halva.any.Any.anyNone;
import static io.soabase.halva.any.Any.anyNull;
import static io.soabase.halva.any.AnyVal.lit;
import static io.soabase.halva.caseclass.AnimalCase.AnimalCase;
import static io.soabase.halva.caseclass.AnimalCase.AnimalCaseMatch;
import static io.soabase.halva.caseclass.ChairCase.ChairCase;
import static io.soabase.halva.caseclass.ChairCase.ChairCaseMatch;
import static io.soabase.halva.caseclass.Value.Value;
import static io.soabase.halva.caseclass.Value.ValueMatch;
import static io.soabase.halva.comprehension.For.forComp;
import static io.soabase.halva.matcher.Matcher.match;
import static io.soabase.halva.sugar.Sugar.List;
import static io.soabase.halva.tuple.Tuple.Pair;
import static io.soabase.halva.tuple.Tuple.Tu;

public class TestMatcher
{
    @Test
    public void testBasic()
    {
        AnyVal<Integer> i = new AnyVal<Integer>(){};

        GenericExampleCase<String, Integer> generic = GenericExampleCase("hey", 100);
        int value = match(generic)
            .caseOf(Tu("hey", i), i::val)
            .caseOf(() -> 0)
            .get();
        Assert.assertEquals(100, value);

        String s = match(generic)
            .caseOf(Tu("hey", i), () -> i.val() > 100, () -> "too big")
            .caseOf(Tu("hey", i), () -> "It's " + i.val())
            .get();
        Assert.assertEquals("It's 100", s);
    }

    @CaseClass interface Value_{int n();}

    int subtract(Value a, Value b)
    {
        AnyVal<Integer> m = new AnyVal<Integer>(){};
        AnyVal<Integer> n = new AnyVal<Integer>(){};

        return match(Pair(a, b))
            .caseOf( Pair(ValueMatch(m), ValueMatch(n)), () -> m.val() - n.val())
            .caseOf( () -> 0)
            .get();
    }

    @Test
    public void testMatchAllPartsOfPair()
    {
        Assert.assertEquals(3, subtract(Value(6), Value(3)));
        Assert.assertEquals(-3, subtract(Value(3), Value(6)));
    }

    static List<Pair<String, Integer>> findMatches(String key, ConsList<Pair<String, Integer>> list) {
        AnyVal<Pair<String, Integer>> foundPair = AnyVal.any();

        return forComp(foundPair, list)
            .filter(() -> foundPair.val()._1.equals(key))
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

        AnyVal<ConsList<Pair>> anyPairList = new AnyVal<ConsList<Pair>>(){};
        AnyVal<?> patternMatcher = Any.anyHeadAnyTail(new AnyVal<Pair>(){}, anyPairList);
        String str = match(list)
            .caseOf(patternMatcher, () -> "The tail is: " + anyPairList.val())
            .get();
        Assert.assertEquals("The tail is: " + list.tail(), str);
    }

    @CaseClass public interface Animal{String name(); int age();}
    @CaseClass public interface Chair{String color(); int legQty(); int age();}

    public int findAnyAge(Object obj)
    {
        AnyVal<String> s = new AnyVal<String>(){};
        AnyVal<Integer> age = new AnyVal<Integer>(){};
        return match(obj)
            .caseOf(AnimalCaseMatch(s, age), age::val)
            .caseOf(ChairCaseMatch(s, lit(3), age), age::val)
            .caseOf(() -> 0)
            .get();
    }

    @Test
    public void testFindAny()
    {
        Assert.assertEquals(14, findAnyAge(AnimalCase("Bobby", 14)));
        Assert.assertEquals(0, findAnyAge(ChairCase("Red", 2, 5)));
        Assert.assertEquals(5, findAnyAge(ChairCase("Red", 3, 5)));
    }

    @Test(expected = MatchError.class)
    public void testMatchError()
    {
        match("test")
            .caseOf("nope", () -> "")
            .get();
    }

    @Test
    public void testMatchNull()
    {
        AnyVal<?> n = anyNull();
        String result = match(null)
            .caseOf(n, () -> "Yes it's null")
            .caseOf(() -> "No it's not null")
            .get();
        Assert.assertEquals("Yes it's null", result);
    }

    @Test
    public void testMatchSome()
    {
        AnyVal<String> s = new AnyVal<String>(){};
        AnyVal<Integer> i = new AnyVal<Integer>(){};
        AnyVal<Integer> some = Any.anySome(i);
        AnyVal<String> someStr = Any.anySome(s);
        String result = match(Optional.of(10))
            .caseOf(someStr, () -> "It's a string: " + s.val())
            .caseOf(some, () -> "It's an int: " + i.val())
            .get();
        Assert.assertEquals("It's an int: 10", result);

        result = match(Optional.of("10"))
            .caseOf(someStr, () -> "It's a string: " + s.val())
            .caseOf(some, () -> "It's an int: " + i.val())
            .get();
        Assert.assertEquals("It's a string: 10", result);
    }

    @Test
    public void testMatchNone()
    {
        String result = match(Optional.empty())
            .caseOf(anyNone(), () -> "It's empty")
            .caseOf(() -> "It's not empty")
            .get();
        Assert.assertEquals("It's empty", result);
    }

    @Test
    public void testBinding()
    {
        AnyVal<String> s = new AnyVal<String>(){};
        AnyVal<Integer> i = new AnyVal<Integer>(){};
        AnyVal<AnimalCase> animal = AnyVal.any();
        AnyVal<ChairCase> chair = AnyVal.any();
        Partial<Object> partial = Matcher.partial()
            .bindTo(animal).caseOf(AnimalCaseMatch(s, i), () -> animal.val().name())
            .bindTo(chair).caseOf(ChairCaseMatch(s, i, i), () -> chair.val().color());

        Assert.assertEquals("bobby", partial.with(AnimalCase("bobby", 10)).get());
        Assert.assertEquals("blue", partial.with(ChairCase("blue", 10, 20)).get());
    }
}

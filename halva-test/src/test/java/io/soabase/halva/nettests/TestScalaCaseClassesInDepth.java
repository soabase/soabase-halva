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

package io.soabase.halva.nettests;

import io.soabase.halva.any.Any;
import io.soabase.halva.any.AnyDeclaration;
import io.soabase.halva.any.AnyType;
import io.soabase.halva.caseclass.CaseClass;
import org.junit.Assert;
import org.junit.Test;

import static io.soabase.halva.matcher.Matcher.match;
import static io.soabase.halva.nettests.PersonCase.PersonCase;
import static io.soabase.halva.nettests.ValueCase.ValueCaseTu;

// from http://www.alessandrolacava.com/blog/scala-case-classes-in-depth/
public class TestScalaCaseClassesInDepth
{
    @CaseClass
    public interface Person
    {
        String lastname();
        String firstname();
        int birthYear();
    }

    @Test
    public void testEquality()
    {
        /*
            val p_1 = Person("Brown", "John", 1969)
            val p_2 = Person("Lacava", "Alessandro", 1976)

            p == p_1 // false
            p == p_2 // true
         */
        PersonCase p = PersonCase("Lacava", "Alessandro", 1976);
        PersonCase p1 = PersonCase("Brown", "John", 1969);
        PersonCase p2 = PersonCase("Lacava", "Alessandro", 1976);
        Assert.assertNotEquals(p, p1);
        Assert.assertEquals(p, p2);

        /*
            // the result is: Person(Lacava,Michele,1972), my brother :)
            val p_3 = p.copy(firstname = "Michele", birthYear = 1972)
         */
        Person p3 = p.copy().firstname("Michele").birthYear(1972).build();
        Assert.assertEquals(p3.toString(), "PersonCase(\"Lacava\", \"Michele\", 1972)");
    }

    public interface Maybe<T>{}
    @CaseClass
    public interface Value<T> extends Maybe<T>{T value();}
    public interface NoValue<T> extends Maybe<T>{}

    /*
        val v: Maybe[Int] = Value(42)
        val v_1: Maybe[Int] = NoValue

        def logValue[T](value: Maybe[T]): Unit = value match {
          case Value(v) => println(s"We have a value here: $v")
          case NoValue => println("I'm sorry, no value")
        }

        logValue(v) // prints We have a value here: 42
        logValue(v_1) // prints I'm sorry, no value
     */
    public <T> String logValue(Maybe<T> value, AnyType<T> typeLiteral)
    {
        Any<T> v = AnyDeclaration.of(typeLiteral).define();
        return match(value)
            .caseOf(ValueCaseTu(v), () -> "We have a value here: " + v.val())
            .caseOf(() -> "I'm sorry, no value")
            .get();
    }

    @Test
    public void testLogValue()
    {
        Maybe<Integer> v = ValueCase.<Integer>builder().value(42).build();
        Maybe<Integer> v1 = new NoValue<Integer>(){};
        AnyType<Integer> typeLiteral = AnyType.get(Integer.class);
        Assert.assertEquals("We have a value here: 42", logValue(v, typeLiteral));
        Assert.assertEquals("I'm sorry, no value", logValue(v1, typeLiteral));
    }
}

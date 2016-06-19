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

import io.soabase.halva.caseclass.CaseClass;
import io.soabase.halva.any.Any;
import io.soabase.halva.any.AnyType;
import org.junit.Assert;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.soabase.halva.matcher.Matcher.match;
import static io.soabase.halva.nettests.PersonTPCase.PersonTPCase;
import static io.soabase.halva.nettests.PersonTPCase.PersonTPCaseTu;

// from http://www.tutorialspoint.com/scala/scala_pattern_matching.htm
public class TestScalaPatternMatchingTutorialsPoint
{
    /*
        object Test {
           def main(args: Array[String]) {
              println(matchTest(3))

           }
           def matchTest(x: Int): String = x match {
              case 1 => "one"
              case 2 => "two"
              case _ => "many"
           }
        }

        C:/>scalac Test.scala
        C:/>scala Test
        many

        C:/>
     */
    public String matchTest1(int x)
    {
        return match(x)
            .caseOf(1, () -> "one")
            .caseOf(2, () -> "two")
            .caseOf(() -> "many")
            .get();
    }

    @Test
    public void testMany()
    {
        Assert.assertEquals("many", matchTest1(3));
    }

    /*
        object Test {
           def main(args: Array[String]) {
              println(matchTest("two"))
              println(matchTest("test"))
              println(matchTest(1))

           }
           def matchTest(x: Any): Any = x match {
              case 1 => "one"
              case "two" => 2
              case y: Int => "scala.Int"
              case _ => "many"
           }
        }

        C:/>scalac Test.scala
        C:/>scala Test
        2
        many
        one
     */

    public Object matchTest2(Object obj)
    {
        Any<Integer> i = new AnyType<Integer>(){};
        return match(obj)
            .caseOf(1, () -> "one")
            .caseOf("two", () -> 2)
            .caseOf(i, Integer.class::getName)
            .caseOf(() -> "many")
            .get();
    }

    @Test
    public void testMany2()
    {
        Assert.assertEquals(2, matchTest2("two"));
        Assert.assertEquals("many", matchTest2("test"));
        Assert.assertEquals("one", matchTest2(1));
        Assert.assertEquals(Integer.class.getName(), matchTest2(10));
    }

    /*
        object Test {
           def main(args: Array[String]) {
            val alice = new Person("Alice", 25)
               val bob = new Person("Bob", 32)
            val charlie = new Person("Charlie", 32)

              for (person <- List(alice, bob, charlie)) {
                 person match {
                    case Person("Alice", 25) => println("Hi Alice!")
                    case Person("Bob", 32) => println("Hi Bob!")
                    case Person(name, age) =>
                       println("Age: " + age + " year, name: " + name + "?")
                 }
              }
           }
           // case class, empty one.
           case class Person(name: String, age: Int)
        }

        C:/>scalac Test.scala
        C:/>scala Test
        Hi Alice!
        Hi Bob!
        Age: 32 year, name: Charlie?
     */
    @CaseClass
    public interface PersonTP{String name(); int age();}

    @Test
    public void testPerson()
    {
        PersonTP alice = PersonTPCase("Alice", 25);
        PersonTP bob = PersonTPCase("Bob", 32);
        PersonTP charlie = PersonTPCase("Charlie", 32);

        Any<String> anyName = new AnyType<String>(){};
        Any<Integer> anyAge = new AnyType<Integer>(){};
        List<String> messages = Arrays.asList(alice, bob, charlie).stream()
            .map(person -> match(person)
                        .caseOf(PersonTPCase("Alice", 25), () -> "Hi Alice!")
                        .caseOf(PersonTPCase("Bob", 32), () -> "Hi Bob!")
                        .caseOf(PersonTPCaseTu(anyName, anyAge), () -> "Age: " + anyAge.val() + " year, name: " + anyName.val() + "?")
                        .<String>get())
            .collect(Collectors.toList());

        List<String> expected = Arrays.asList("Hi Alice!", "Hi Bob!", "Age: 32 year, name: Charlie?");
        Assert.assertEquals(expected, messages);
    }
}

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
package io.soabase.halva.comprehension;

import io.soabase.halva.any.AnyVal;
import io.soabase.halva.caseclass.CaseClass;
import io.soabase.halva.tuple.Tuple;
import org.junit.Assert;
import org.junit.Test;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.soabase.halva.comprehension.Author.Author;
import static io.soabase.halva.comprehension.Book.Book;
import static io.soabase.halva.comprehension.For.forComp;
import static io.soabase.halva.sugar.Sugar.List;
import static io.soabase.halva.tuple.Tuple.Tu;

public class TestFor
{
    @Test
    public void testSimple()
    {
/*
val one = "This checks for scala or sbt patterns"
val two = "java rocks"
val sentences = List(one, two)
val dict = List("scala", "sbt", "patterns")

val result = for {
    sentence <- sentences
    does = dict forall sentence.contains
} yield (sentence, does)
         */

        String one = "This checks for scala or sbt patterns";
        String two = "java rocks";
        List<String> sentences = List(one, two);
        List<String> dict = List("scala", "sbt", "patterns");
        AnyVal<String> sentence = AnyVal.any();
        AnyVal<Boolean> does = AnyVal.any();

        List<Tuple> ts = forComp(sentence, sentences)
            .letComp(does, () -> dict.contains(sentence.val()))
            .yield(() -> Tu(sentence.val(), does.val()));
        System.out.println(ts);
    }

    @Test
    public void testBasic()
    {
        List<List> lists1 = List(List(1, 2, 3), List("A", "B", "C"));
        List<List> lists2 = List(List(4, 5, 6), List("D", "E", "F"));
        List<List<List>> big = List(lists1, lists2);

        AnyVal<List<List>> lol = AnyVal.any();
        AnyVal<List> l = AnyVal.any();
        AnyVal<Object> o = AnyVal.any();
        List<String> s = forComp(lol, big)
              .forComp(l, lol::val)
              .forComp(o, l::val)
              .yield(() -> String.valueOf(o.val()));
        Assert.assertEquals(List("1", "2", "3", "A", "B", "C", "4", "5", "6", "D", "E", "F"), s);
    }

    @CaseClass interface Author_{String name(); List<Integer> years();}
    @CaseClass interface Book_{List<Author> authors(); String title();}

    @Test
    public void testComplex()
    {
        Author aynRand = Author("Ayn Rand", List(1940, 1950));
        Author kenFollet = Author("Ken Follet", List(1960, 1970, 1980));
        Author leeChild = Author("Lee Child", List(1990, 2000));

        List<Book> books = List(
            Book(List(aynRand, kenFollet, leeChild), "Big Compilation"),
            Book(List(aynRand), "Atlas Shrugged"),
            Book(List(kenFollet), "The Pillars of the Earth")
                               );

    /*
        for {
            book <- books
            if book.authors.length == 1
            author <- book.authors
            if author.name == "Ayn Rand"
            year <- author.years
        } yield (book.title, year)
     */
        {
            AnyVal<Book> book = AnyVal.any();
            AnyVal<Author> author = AnyVal.any();
            AnyVal<Integer> year = AnyVal.any();

            List<Tuple> result = forComp(book, books)
                .filter(() -> book.val().authors().size() == 1)
                .forComp(author, () -> book.val().authors())
                .filter(() -> author.val().name().startsWith("Ayn"))
                .forComp(year, () -> author.val().years())
                .yield(() -> Tu(book.val().title(), year.val()));

            Assert.assertEquals(List(Tu("Atlas Shrugged", 1940), Tu("Atlas Shrugged", 1950)), result);
        }
    }

    /*
    def even(from: Int, to: Int): List[Int] =
        for (i <- List.range(from, to) if i % 2 == 0) yield i
    */
    List<Integer> even(int from, int to)
    {
        AnyVal<Integer> i = AnyVal.any();
        return forComp(i, IntStream.range(from, to))
            .filter(() -> i.val() % 2 == 0)
            .yield(i::val);
    }

    @Test
    public void testEven()
    {
        Assert.assertEquals(List(0, 2, 4, 6, 8, 10, 12, 14, 16, 18), even(0, 20));
    }

    /*
        def foo(n: Int, v: Int) =
        for (i <- 0 until n;
             j <- i until n if i + j == v) yield
          Pair(i, j)
     */
    List<Tuple> foo(int n, int v)
    {
        AnyVal<Integer> i = AnyVal.any();
        AnyVal<Integer> j = AnyVal.any();
        return forComp(i, IntStream.range(0, n))
            .forCompInt(j, () -> IntStream.range(i.val(), n))
            .filter(() -> i.val() + j.val() == v)
            .yield(() -> Tu(i.val(), j.val()));
    }

    @Test
    public void testFoo()
    {
        Assert.assertEquals(List(Tu(13, 19), Tu(14, 18), Tu(15, 17), Tu(16, 16)), foo(20, 32));
    }

    /*
        for (i <- Iterator.range(0, 20);
               j <- Iterator.range(i, 20) if i + j == 32)
            println("(" + i + ", " + j + ")")
     */
    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    @Test
    public void testUnit()
    {
        AnyVal<Integer> i = AnyVal.any();
        AnyVal<Integer> j = AnyVal.any();
        StringBuilder str = new StringBuilder();
        forComp(i, IntStream.range(0, 20))
            .forCompInt(j, () -> IntStream.range(i.val(), 20))
            .filter(() -> i.val() + j.val() == 32)
            .unit(() -> str.append("(" + i.val() + ", " + j.val() + ")"));
        Assert.assertEquals("(13, 19)(14, 18)(15, 17)(16, 16)", str.toString());
    }

    @Test
    public void testIntStream()
    {
        AnyVal<Integer> i = AnyVal.any();
        AnyVal<Integer> j = AnyVal.any();
        AnyVal<Integer> from = AnyVal.any();
        List<Integer> result = forComp(i, IntStream.rangeClosed(1, 3))
            .letComp(from, () -> 4 - i.val())
            .forCompInt(j, () -> IntStream.rangeClosed(from.val(), 3))
            .yield(() -> (10 * i.val() + j.val()));
        Assert.assertEquals(List(13, 22, 23, 31, 32, 33), result);
    }

    @Test
    public void testStreamOverYield()
    {
        AnyVal<List<Integer>> i = AnyVal.any();
        AnyVal<Integer> j = AnyVal.any();
        List<String> s = forComp(i, List(List(1, 2), List(5, 6)))
            .forComp(j, i::val)
            .stream(() -> 10 * j.val())
            .map(x -> "=" + x)
            .collect(Collectors.toList());
        Assert.assertEquals(List("=10", "=20", "=50", "=60"), s);
    }
}

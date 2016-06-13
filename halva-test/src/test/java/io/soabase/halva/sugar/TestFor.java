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
package io.soabase.halva.sugar;

import io.soabase.halva.any.Any;
import io.soabase.halva.any.AnyDeclaration;
import io.soabase.halva.any.AnyType;
import io.soabase.halva.caseclass.CaseClass;
import io.soabase.halva.tuple.Tuple;
import org.junit.Assert;
import org.junit.Test;
import java.util.List;
import java.util.stream.IntStream;

import static io.soabase.halva.any.AnyDeclaration.anyBoolean;
import static io.soabase.halva.any.AnyDeclaration.anyString;
import static io.soabase.halva.comprehension.For.For;
import static io.soabase.halva.sugar.Author.Author;
import static io.soabase.halva.sugar.Book.Book;
import static io.soabase.halva.sugar.Sugar.List;

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
        Any<String> sentence = anyString.define();
        Any<Boolean> does = anyBoolean.define();

        List<Tuple> ts = For(sentence, sentences)
            .set(() -> does.set(dict.contains(sentence.val())))
            .yield(() -> Tuple.Tu(sentence.val(), does.val()));
        System.out.println(ts);
    }

    @Test
    public void testBasic()
    {
        List<List<?>> lists1 = List(List(1, 2, 3), List("A", "B", "C"));
        List<List<?>> lists2 = List(List(4, 5, 6), List("D", "E", "F"));
        List<List<List<?>>> big = List(lists1, lists2);

        Any<List<List<?>>> lol = AnyDeclaration.of(new AnyType<List<List<?>>>(){}).define();
        Any<List<?>> l = AnyDeclaration.of(new AnyType<List<?>>(){}).define();
        Any<Object> o = AnyDeclaration.of(Object.class).define();
        List<String> s = For(lol, big)
              .and(l, lol::val)
              .and(o, l::val)
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
            Any<Book> book = AnyDeclaration.of(Book.class).define();
            Any<Author> author = AnyDeclaration.of(Author.class).define();
            Any<Integer> year = AnyDeclaration.of(Integer.class).define();

            List<Tuple> result = For(book, books)
                .when(() -> book.val().authors().size() == 1)
                .and(author, () -> book.val().authors())
                .when(() -> author.val().name().startsWith("Ayn"))
                .and(year, () -> author.val().years())
                .yield(() -> Tuple.Tu(book.val().title(), year.val()));

            Assert.assertEquals(List(Tuple.Tu("Atlas Shrugged", 1940), Tuple.Tu("Atlas Shrugged", 1950)), result);
        }
    }

    /*
    def even(from: Int, to: Int): List[Int] =
        for (i <- List.range(from, to) if i % 2 == 0) yield i
    */
    List<Integer> even(int from, int to)
    {
        Any<Integer> i = AnyDeclaration.of(Integer.class).define();
        return For(i, IntStream.range(from, to))
            .when(() -> i.val() % 2 == 0)
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
        Any<Integer> i = AnyDeclaration.of(Integer.class).define();
        Any<Integer> j = AnyDeclaration.of(Integer.class).define();
        return For(i, IntStream.range(0, n))
            .andInt(j, () -> IntStream.range(i.val(), n))
            .when(() -> i.val() + j.val() == v)
            .yield(() -> Tuple.Tu(i.val(), j.val()));
    }

    @Test
    public void testFoo()
    {
        Assert.assertEquals(List(Tuple.Tu(13, 19), Tuple.Tu(14, 18), Tuple.Tu(15, 17), Tuple.Tu(16, 16)), foo(20, 32));
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
        Any<Integer> i = AnyDeclaration.of(Integer.class).define();
        Any<Integer> j = AnyDeclaration.of(Integer.class).define();
        StringBuilder str = new StringBuilder();
        For(i, IntStream.range(0, 20))
            .andInt(j, () -> IntStream.range(i.val(), 20))
            .when(() -> i.val() + j.val() == 32)
            .unit(() -> str.append("(" + i.val() + ", " + j.val() + ")"));
        Assert.assertEquals("(13, 19)(14, 18)(15, 17)(16, 16)", str.toString());
    }
}

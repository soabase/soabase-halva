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
import io.soabase.halva.any.AnyVal;
import io.soabase.halva.tuple.details.Tuple2;
import org.junit.Assert;
import org.junit.Test;
import java.util.List;
import java.util.stream.IntStream;

import static io.soabase.halva.comprehension.For.forComp;
import static io.soabase.halva.sugar.Sugar.List;
import static io.soabase.halva.tuple.Tuple.Tu;

// from http://daily-scala.blogspot.com/2009/08/for-comprehensions.html
public class Test200908ForComprehensions
{
    AnyVal<Integer> i = Any.make();
    AnyVal<Integer> j = Any.make();
    AnyVal<Integer> k = Any.make();

    List<Integer> range = List(1, 2, 3, 4, 5);

    // for( i <- range ) yield i+1
    @Test
    public void testYieldLine11()
    {
        List<Integer> result = forComp(i, range).yield(() -> i.val() + 1);
        Assert.assertEquals(List(2, 3, 4, 5, 6), result);
    }

    // for( i <- range; if( i % 2 == 0) ) yield i
    @Test
    public void testFilterLine19()
    {
        List<Integer> result = forComp(i, range).filter(() -> i.val() % 2 == 0).yield(() -> i.val());
        Assert.assertEquals(List(2, 4), result);
    }

    // for( i <- range; j <- range) yield (i,j)
    @Test
    public void testMultipleGeneratorsLine27()
    {
        List<Tuple2<Integer, Integer>> result = forComp(i, range)
            .forComp(j, () -> range)
            .yield(() -> Tu(i.val(), j.val()));
        Assert.assertEquals(List(Tu(1,1), Tu(1,2), Tu(1,3), Tu(1,4), Tu(1,5), Tu(2,1), Tu(2,2),
            Tu(2,3), Tu(2,4), Tu(2,5), Tu(3,1), Tu(3,2), Tu(3,3), Tu(3,4), Tu(3,5), Tu(4,1), Tu(4,2),
            Tu(4,3), Tu(4,4), Tu(4,5), Tu(5,1), Tu(5,2), Tu(5,3), Tu(5,4), Tu(5,5)),
        result);
    }

    // for( i <- range; j <- 1 to i; k = i-j) yield k
    @Test
    public void testDeclareVariablesLine32()
    {
        List<Integer> result = forComp(i, range)
            .forCompInt(j, () -> IntStream.rangeClosed(1, i.val()))
            .set(() -> k.set(i.val() - j.val()))
            .yield(() -> k.val());
        Assert.assertEquals(List(0, 1, 0, 2, 1, 0, 3, 2, 1, 0, 4, 3, 2, 1, 0), result);
    }
}

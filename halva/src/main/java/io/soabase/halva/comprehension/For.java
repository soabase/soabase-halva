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

import io.soabase.halva.any.Match;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public interface For extends ForNext
{
    /**
     * Start a For comprehension for the given collection. Each iterated
     * item will be stored in the given any value
     *
     * @param any box to store iterated items
     * @param iterator collection to iterate over
     * @return new for comprehension
     */
    static <T, R> ForNext forComp(Match<T> any, Iterable<? extends R> iterator)
    {
        return new ForImpl(any, iterator);
    }

    /**
     * Start a For comprehension for the given collection. Each iterated
     * item will be stored in the given any value
     *
     * @param any box to store iterated items
     * @param stream collection to iterate over
     * @return new for comprehension
     */
    static <T> ForNext forComp(Match<T> any, IntStream stream)
    {
        return new ForImpl(any, stream::iterator);
    }

    /**
     * Start a For comprehension for the given collection. Each iterated
     * item will be stored in the given any value
     *
     * @param any box to store iterated items
     * @param stream collection to iterate over
     * @return new for comprehension
     */
    static <T> ForNext forComp(Match<T> any, LongStream stream)
    {
        return new ForImpl(any, stream::iterator);
    }

    /**
     * Start a For comprehension for the given collection. Each iterated
     * item will be stored in the given any value
     *
     * @param any box to store iterated items
     * @param stream collection to iterate over
     * @return new for comprehension
     */
    static <T> ForNext forComp(Match<T> any, DoubleStream stream)
    {
        return new ForImpl(any, stream::iterator);
    }
}

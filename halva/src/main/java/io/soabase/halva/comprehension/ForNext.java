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
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public interface ForNext
{
    /**
     * Add another instance to the comprehension
     *
     * @param any box to store iterated items
     * @param stream function that returns a stream
     * @return this
     */
    <T> ForNext forComp(Match<T> any, Supplier<Iterable<T>> stream);

    /**
     * Add another instance to the comprehension
     *
     * @param any box to store iterated items
     * @param stream function that returns a stream
     * @return this
     */
    ForNext forCompInt(Match<Integer> any, Supplier<IntStream> stream);

    /**
     * Add another instance to the comprehension
     *
     * @param any box to store iterated items
     * @param stream function that returns a stream
     * @return this
     */
    ForNext forCompLong(Match<Long> any, Supplier<LongStream> stream);

    /**
     * Add another instance to the comprehension
     *
     * @param any box to store iterated items
     * @param stream function that returns a stream
     * @return this
     */
    ForNext forCompDouble(Match<Double> any, Supplier<DoubleStream> stream);

    /**
     * Allows setting of an any value (or the current set of values) at the
     * current state of the stream
     *
     *
     * @param any value to set
     * @param valueSupplier function that return the value to assign
     * @return this
     */
    <T> ForNext letComp(Match<T> any, Supplier<T> valueSupplier);

    /**
     * Add filtering to the stream. The test can examine the current state
     * of the any values and return true to accept the current cummulative set of
     * values or false to reject.
     *
     * @param test the tester function
     * @return this
     */
    ForNext filter(Supplier<Boolean> test);

    /**
     * Return a stream view of the for-comprehension so that it can be pipelined
     * with other streams.
     *
     * @param yielder a function that will get called for the cartesian product of the added
     *                containers and produces a value for each iteration
     * @return the stream
     */
    <T> Stream<T> stream(Supplier<T> yielder);

    /**
     * Process the added collections and call the given function for the cartesian
     * product to produce a value for each iteration. The produces values are collected
     * into a List that is returned. This is a <em>terminal operation</em>.
     *
     * @param yielder a function that will get called for the cartesian product of the added
     *                containers and produces a value for each iteration
     * @return List of produced items
     */
    <T> List<T> yield(Supplier<T> yielder);

    /**
     * Convenience utility. Calls {@link #yield(Supplier)} and returns the one item yielded. If more
     * than one item is yielded, <code>IndexOutOfBoundsException</code> is thrown.
     *
     * @param yielder a function that will get called for the cartesian product of the added
     *                containers to produces a single value
     * @return item produced
     * @throws IndexOutOfBoundsException if more than one item is produced
     */
    <T> T yield1(Supplier<T> yielder);

    /**
     * Convenience method - Calls {@link #yield(Supplier)} and ignores any produced values
     */
    void unit();

    /**
     * Same as {@link #yield(Supplier)} but calls the given consumer for each iterated item and
     * produces no result.
     *
     * @param consumer handler for each item
     */
    void unit(Runnable consumer);
}

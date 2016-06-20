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

import io.soabase.halva.any.Any;
import io.soabase.halva.any.AnyVal;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

@SuppressWarnings("MethodNameSameAsClassName")
public interface For
{
    static <T, R> For forComp(AnyVal<T> any, Iterable<? extends R> iterator)
    {
        return new ForImpl(any, iterator);
    }

    static <T> For forComp(AnyVal<T> any, IntStream stream)
    {
        return new ForImpl(any, stream::iterator);
    }

    static <T> For forComp(AnyVal<T> any, LongStream stream)
    {
        return new ForImpl(any, stream::iterator);
    }

    static <T> For forComp(AnyVal<T> any, DoubleStream stream)
    {
        return new ForImpl(any, stream::iterator);
    }

    <T, R> For forComp(AnyVal<T> any, Supplier<Iterable<? extends R>> stream);

    <T> For forCompInt(AnyVal<T> any, Supplier<IntStream> stream);

    <T> For forCompLong(AnyVal<T> any, Supplier<LongStream> stream);

    <T> For forCompDouble(AnyVal<T> any, Supplier<DoubleStream> stream);

    For filter(SimplePredicate test);

    For set(Runnable value);

    <T> Stream<T> stream(Supplier<T> yielder);

    <T> Stream<T> stream();

    <T> List<T> yield(Supplier<T> yielder);

    <T> T yield1(Supplier<T> yielder);

    void unit();

    void unit(Runnable consumer);
}

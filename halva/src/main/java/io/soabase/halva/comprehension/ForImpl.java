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
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@SuppressWarnings("unchecked")
class ForImpl implements For
{
    private final StreamFor streamFor;

    ForImpl(AnyVal any, Iterable stream)
    {
        streamFor = StreamFor.start().forComp(any, () -> StreamSupport.stream(stream.spliterator(), false));
    }

    @Override
    public <T> For forComp(AnyVal<T> any, Supplier<Iterable<T>> stream)
    {
        streamFor.forComp(any, () -> StreamSupport.stream(stream.get().spliterator(), false));
        return this;
    }

    @Override
    public For forCompInt(AnyVal<Integer> any, Supplier<IntStream> stream)
    {
        streamFor.forComp(any, () -> StreamSupport.stream(stream.get().spliterator(), false));
        return this;
    }

    @Override
    public For forCompLong(AnyVal<Long> any, Supplier<LongStream> stream)
    {
        streamFor.forComp(any, () -> StreamSupport.stream(stream.get().spliterator(), false));
        return this;
    }

    @Override
    public For forCompDouble(AnyVal<Double> any, Supplier<DoubleStream> stream)
    {
        streamFor.forComp(any, () -> StreamSupport.stream(stream.get().spliterator(), false));
        return this;
    }

    @Override
    public <T> For letComp(AnyVal<T> any, Supplier<T> valueSupplier)
    {
        streamFor.letComp(any, valueSupplier);
        return this;
    }

    @Override
    public For filter(Supplier<Boolean> test)
    {
        streamFor.filter(test);
        return this;
    }

    @Override
    public <T> Stream<T> stream(Supplier<T> yielder)
    {
        return streamFor.yield(yielder);
    }

    @Override
    public <T> List<T> yield(Supplier<T> yielder)
    {
        return stream(yielder).collect(Collectors.toList());
    }

    @Override
    public <T> T yield1(Supplier<T> yielder)
    {
        List<T> list = yield(yielder);
        int size = (list != null) ? list.size() : 0;
        if ( size != 1 )
        {
            throw new IndexOutOfBoundsException("expression resulted in " + size + " items, not 1");
        }
        return list.get(0);
    }

    @Override
    public void unit()
    {
        stream(() -> "").forEach(x -> {});
    }

    @Override
    public void unit(Runnable consumer)
    {
        stream(() -> "").forEach(x -> consumer.run());
    }
}

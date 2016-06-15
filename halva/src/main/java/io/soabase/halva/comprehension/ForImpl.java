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
import java.util.ArrayList;
import java.util.Iterator;
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
    private final List<Entry> entries = new ArrayList<>();

    private static class Entry
    {
        final Any any;
        final Supplier<Iterable<?>> stream;
        final List<SimplePredicate> predicates = new ArrayList<>();

        Entry(Any any, Supplier<Iterable<?>> stream)
        {
            this.any = any;
            this.stream = stream;
        }
    }

    ForImpl(Any any, Iterable stream)
    {
        if ( any == null )
        {
            throw new IllegalArgumentException("any cannot be null");
        }
        if ( stream == null )
        {
            throw new IllegalArgumentException("stream cannot be null");
        }
        entries.add(new Entry(any, () -> stream));
    }

    @Override
    public <T, R> For forComp(Any<T> any, Supplier<Iterable<? extends R>> stream)
    {
        if ( any == null )
        {
            throw new IllegalArgumentException("any cannot be null");
        }
        if ( stream == null )
        {
            throw new IllegalArgumentException("stream cannot be null");
        }
        //noinspection Convert2MethodRef
        Supplier<Iterable<?>> wrapped = () -> stream.get();
        entries.add(new Entry(any, wrapped));
        return this;
    }

    @Override
    public <T> For forCompInt(Any<T> any, Supplier<IntStream> stream)
    {
        return forComp(any, () -> () -> stream.get().iterator());
    }

    @Override
    public <T> For forCompLong(Any<T> any, Supplier<LongStream> stream)
    {
        return forComp(any, () -> () -> stream.get().iterator());
    }

    @Override
    public <T> For forCompDouble(Any<T> any, Supplier<DoubleStream> stream)
    {
        return forComp(any, () -> () -> stream.get().iterator());
    }

    @Override
    public For filter(SimplePredicate test)
    {
        if ( test == null )
        {
            throw new IllegalArgumentException("test cannot be null");
        }
        getPreviousEntry().predicates.add(test);
        return this;
    }

    @Override
    public <T> For set(Runnable value)
    {
        if ( value == null )
        {
            throw new IllegalArgumentException("value cannot be null");
        }
        Entry previousEntry = getPreviousEntry();
        previousEntry.predicates.add(() -> {
            value.run();
            return true;
        });
        return this;
    }

    @Override
    public <T> T yield1(Supplier<T> yielder)
    {
        List<T> list = yieldLoop(yielder, null);
        int size = (list != null) ? list.size() : 0;
        if ( size != 1 )
        {
            throw new IndexOutOfBoundsException("expression resulted in " + size + " items, not 1");
        }
        return list.get(0);
    }

    @Override
    public <T> List<T> yield(Supplier<T> yielder)
    {
        return yieldLoop(yielder, null);
    }

    @Override
    public void unit()
    {
        yieldLoop(null, null);
    }

    @Override
    public void unit(Runnable consumer)
    {
        yieldLoop(null, consumer);
    }

    private Entry getPreviousEntry()
    {
        if ( entries.size() == 0 )
        {
            throw new IllegalStateException("No generators to apply to");
        }
        return entries.get(entries.size() - 1);
    }

    private static void nop(Object o){}

    private <T> List<T> yieldLoop(Supplier<T> yielder, Runnable consumer)
    {
        Stream<T> stream = yieldLoop(0, yielder, consumer);
        if ( yielder != null )
        {
            return stream.collect(Collectors.toList());
        }
        stream.forEach(ForImpl::nop);
        return null;
    }

    private Stream yieldLoop(int index, Supplier<?> yielder, Runnable consumer)
    {
        Entry entry = entries.get(index);
        Stream stream = makeWorker(entry);
        if ( (index + 1) < entries.size() )
        {
            stream = stream.flatMap(o -> yieldLoop(index + 1, yielder, consumer));
            stream = checkFilters(entry, stream);
        }
        else
        {
            stream = checkFilters(entry, stream);
            stream = stream.map(o -> {
                if ( yielder != null )
                {
                    return yielder.get();
                }
                if ( consumer != null )
                {
                    consumer.run();
                }
                return o;
            });
        }
        return stream;
    }

    private Stream checkFilters(Entry entry, Stream stream)
    {
        if ( entry.predicates.size() != 0 )
        {
            stream = stream.filter(ignore -> entry.predicates.stream().allMatch(SimplePredicate::test));
        }
        return stream;
    }

    private Stream makeWorker(Entry entry)
    {
        Iterable worker = () -> new Iterator()
        {
            private Iterator actual;
            private void check()
            {
                if ( actual == null )
                {
                    actual = entry.stream.get().iterator();
                }
            }

            @Override
            public boolean hasNext()
            {
                check();
                return actual.hasNext();
            }

            @Override
            public Object next()
            {
                Object next = actual.next();
                entry.any.set(next);
                return next;
            }
        };
        return StreamSupport.stream(worker.spliterator(), false);
    }
}

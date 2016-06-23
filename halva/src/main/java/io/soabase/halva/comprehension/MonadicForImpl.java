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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Implementation for inner-type-less MonadicFor
 */
public class MonadicForImpl<M>
{
    public enum Method
    {
        MAPPED_SETTERS,
        INLINE_SETTERS // requires filter support
    }

    public <R> MonadicForImpl(AnyVal<R> any, M startingMonad, MonadicForWrapper<M> wrapper, Method method)
    {
        this.wrapper = wrapper;
        this.method = method;
        entries.add(new Entry<>(any, () -> startingMonad, null));
    }

    /////////////////////////////////////////////////////////////

    @SuppressWarnings("unchecked")
    public <R> MonadicForImpl<M> forComp(AnyVal<R> any, Supplier<? extends M> monadSupplier)
    {
        entries.add(new Entry(any, monadSupplier, null));
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> MonadicForImpl<M> letComp(AnyVal<T> any, Supplier<T> letSupplier)
    {
        if ( method == Method.INLINE_SETTERS )
        {
            filter(() -> {
                any.set(letSupplier.get());
                return true;
            });
        }
        else
        {
            entries.add(new Entry(any, null, letSupplier));
        }
        return this;
    }

    public MonadicForImpl<M> filter(Supplier<Boolean> test)
    {
        getPreviousEntry().predicates.add(test);
        return this;
    }

    public <R> M yield(Supplier<R> yieldSupplier)
    {
        if ( method == Method.INLINE_SETTERS )
        {
            return yieldLoopInline(0, yieldSupplier);
        }
        return yieldLoopMapped(0, yieldSupplier, null);
    }

    /////////////////////////////////////////////////////////////

    // INTERNALS

    private final MonadicForWrapper<M> wrapper;
    private final Method method;

    // From ForImpl

    private final List<Entry<M>> entries = new ArrayList<>();

    private static class Entry<M>
    {
        final Any any;
        final Supplier<M> monadSupplier;
        final Supplier<?> setter;
        final List<Supplier<Boolean>> predicates = new ArrayList<>();

        Entry(Any any, Supplier<M> stream, Supplier<?> setter)
        {
            if ( (stream != null) && (setter != null) )
            {
                throw new IllegalStateException("Internal error. Can't have both a stream and a setter");
            }
            this.any = any;
            this.monadSupplier = stream;
            this.setter = setter;
        }
    }

    private Entry<M> getPreviousEntry()
    {
        if ( entries.size() == 0 )
        {
            throw new IllegalStateException("No generators to apply to");
        }
        return entries.get(entries.size() - 1);
    }

    @SuppressWarnings("unchecked")
    private M yieldLoopInline(int index, Supplier<?> yielder)
    {
        Entry<M> entry = entries.get(index);
        M stream;
        if ( entry.monadSupplier != null )
        {
            stream = wrapper.filter(entry.monadSupplier.get(), o -> {
                entry.any.set(o);
                return true;
            });
        }
        else
        {
            throw new IllegalStateException("inline yield does not support setter");
        }

        if ( entry.predicates.size() != 0 )
        {
            stream = wrapper.filter(stream, o -> entry.predicates.stream().allMatch(Supplier::get));
        }

        if ( (index + 1) < entries.size() )
        {
            stream = wrapper.flatMap(stream, o -> yieldLoopInline(index + 1, yielder));
        }
        else
        {
            stream = wrapper.map(stream, o -> yielder.get());
        }
        return stream;
    }

    @SuppressWarnings("unchecked")
    private M yieldLoopMapped(int index, Supplier<?> yielder, M prevStream)
    {
        final Entry<M> entry = entries.get(index);
        M monad = prevStream;
        if ( null != entry.monadSupplier )
        {
            monad = entry.monadSupplier.get();
            wrapper.map(monad, o -> {
                entry.any.set(o);
                return o;
            });
        }
        else if ( null != entry.setter )
        { // this is a setter
            monad = prevStream;
            wrapper.map(monad, o -> {
                entry.any.set(entry.setter.get());
                return o;
            });
        }

        // Test:
        for ( Supplier<Boolean> test : entry.predicates )
        {
            monad = wrapper.filter(monad, __ -> test.get());
        }
        // Map
        if ( (index + 1) < entries.size() )
        {
            final M nextStageMonad = monad;
            monad = wrapper.flatMap(monad, o -> yieldLoopMapped(index + 1, yielder, nextStageMonad));
        }
        else
        {
            monad = wrapper.map(monad, o -> yielder.get());
        }
        return monad;
    }

}


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
package io.soabase.halva.matcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

class Getter<ARG> implements GettersBase
{
    private final List<Entry<ARG>> entries = new ArrayList<>();
    private final Optional<ARG> arg;
    private volatile Entry<ARG> defaultEntry;

    static class Entry<ARG>
    {
        final Function<ARG, Optional<? extends Supplier<?>>> curry;

        public Entry(ExtractObject extracter, Supplier<?> extracterProc)
        {
            if ( extracter == null )
            {
                throw new IllegalArgumentException("extracter cannot be null");
            }
            if ( extracterProc == null )
            {
                throw new IllegalArgumentException("proc cannot be null");
            }
            curry = arg -> extracter.extract(arg) ? Optional.of(extracterProc::get) : Optional.empty();
        }

        Entry(Supplier<?> proc)
        {
            if ( proc == null )
            {
                throw new IllegalArgumentException("proc cannot be null");
            }
            this.curry = arg -> Optional.of(proc);
        }
    }

    Getter(ARG arg)
    {
        this.arg = Optional.ofNullable(arg);
    }

    Getter(ARG arg, Getter<ARG> rhs)
    {
        this.defaultEntry = rhs.defaultEntry;
        this.arg = Optional.ofNullable(arg);
        this.entries.addAll(rhs.entries);
    }

    void addEntry(Entry<ARG> entry)
    {
        entries.add(entry);
    }

    void setDefault(Supplier<?> proc)
    {
        if ( proc == null )
        {
            throw new IllegalArgumentException("proc cannot be null");
        }
        if ( defaultEntry != null )
        {
            throw new IllegalArgumentException("A default case has already been added");
        }
        defaultEntry = new Entry<>(proc);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<T> getOpt()
    {
        ARG localArg = getArg();

        Optional<? extends Supplier<?>> first = entries.stream()
            .map(entry -> entry.curry.apply(localArg))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst();

        if ( first.isPresent() )
        {
            return Optional.ofNullable((T)first.get().get());
        }
        if ( defaultEntry != null )
        {
            Optional<? extends Supplier<?>> supplier = defaultEntry.curry.apply(localArg);
            if ( supplier.isPresent() )
            {
                return Optional.ofNullable((T)supplier.get().get());
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get()
    {
        return (T)getOpt().orElseThrow(() -> new MatchError("No matches found and no default provided for: " + getArg()));
    }

    @Override
    public void apply()
    {
        getOpt();
    }

    ARG getArg()
    {
        return this.arg.orElseThrow(() -> new UnsupportedOperationException("Partial is being called without an argument"));
    }
}

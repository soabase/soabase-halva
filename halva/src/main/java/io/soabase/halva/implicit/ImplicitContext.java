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
package io.soabase.halva.implicit;

import io.soabase.halva.any.AnyDeclaration;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Function;
import java.util.function.Supplier;

class ImplicitContext
{
    private final ConcurrentLinkedDeque<ContextLevel> contexts;

    ImplicitContext()
    {
        this.contexts = new ConcurrentLinkedDeque<>();
    }

    boolean push()
    {
        if ( contexts.size() == Integer.MAX_VALUE )
        {
            return false;
        }
        contexts.push(new ContextLevel());
        return true;
    }

    boolean pop()
    {
        if ( contexts.size() < 2 )
        {
            return false;
        }
        contexts.pop();
        return true;
    }

    <FROM, TO> void setConversion(Function<FROM, TO> converter, AnyDeclaration<FROM> fromType, AnyDeclaration<TO> toType)
    {
        contexts.getFirst().setConversion(converter, fromType, toType);
    }

    <FROM, TO> Optional<TO> convert(FROM from, AnyDeclaration<TO> toType)
    {
        for ( ContextLevel level : contexts )
        {
            Optional<Function> conversion = level.getConversion(from, toType);
            if ( conversion.isPresent() )
            {
                //noinspection unchecked
                return Optional.of((TO)conversion.get().apply(from));
            }
        }
        return Optional.empty();
    }

    <T> void set(AnyDeclaration<T> key, Supplier<T> valueSupplier)
    {
        contexts.getFirst().set(key, valueSupplier);
    }

    @SuppressWarnings("unchecked")
    <T> T get(AnyDeclaration<T> key, T defaultValue)
    {
        for ( ContextLevel level : contexts )
        {
            Object value = level.get(key);
            if ( value != null )
            {
                return (T)value;
            }
        }
        return defaultValue;
    }
}

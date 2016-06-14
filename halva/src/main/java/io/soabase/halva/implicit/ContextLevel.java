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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

class ContextLevel
{
    private final ConcurrentMap<AnyDeclaration, Object> values = new ConcurrentHashMap<>();
    private final ConcurrentMap<AnyDeclaration, Conversion> conversions = new ConcurrentHashMap<>();

    static class Conversion
    {
        private final Function converter;
        private final AnyDeclaration toType;

        private Conversion(Function converter, AnyDeclaration toType)
        {
            this.converter = converter;
            this.toType = toType;
        }

        Function getConverter()
        {
            return converter;
        }

        AnyDeclaration getToType()
        {
            return toType;
        }
    }

    void set(AnyDeclaration key, Object value)
    {
        values.put(key, value);
    }

    Object get(AnyDeclaration key)
    {
        return values.get(key);
    }

    void setConversion(Function converter, AnyDeclaration fromType, AnyDeclaration toType)
    {
        conversions.put(fromType, new Conversion(converter, toType));
    }

    Optional<Function> getConversion(Object from, AnyDeclaration toType)
    {
/*
        return conversions.entrySet().stream()
            .filter(e -> e.getKey().isAssignableFrom(from) && e.getValue().getToType().equals(toType))
            .map(e -> e.getValue().getConverter())
            .findFirst();
*/
        return Optional.empty();    // TODO
    }
}

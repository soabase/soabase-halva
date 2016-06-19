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
package io.soabase.halva.any;

import io.soabase.halva.alias.TypeAliasType;
import io.soabase.halva.sugar.ConsList;

public interface Any<T>
{
    static <T> AnyVal<T> make()
    {
        return new AnyVal<>();
    }

    static <T> AnyList headTail(T head, ConsList<T> tail)
    {
        return new AnyConsImpl(head, null, tail, null);
    }

    static <T> AnyList headAnyTail(T head, Any<? extends ConsList<? extends T>> tail)
    {
        return new AnyConsImpl(head, null, null, tail);
    }

    static <T> AnyList anyHeadTail(Any<T> head, ConsList<T> tail)
    {
        return new AnyConsImpl(null, head, tail, null);
    }

    static <T> AnyList anyHeadAnyTail(Any<T> head, Any<? extends ConsList<? extends T>> tail)
    {
        return new AnyConsImpl(null, head, null, tail);
    }

    static <T extends REAL, REAL> Any<T> typeAlias(TypeAliasType<REAL, T> typeAliasType)
    {
        return new AnyImpl<>(null, typeAliasType);
    }

    T val();

    void set(T value);

    boolean canSet(T value);
}

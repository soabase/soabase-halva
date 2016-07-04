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
import java.util.List;
import java.util.Optional;

/**
 * Factory for making {@link AnyVal} instances
 */
public interface Any
{
    /**
     * Return a new AnyList that matches the given head of a list and the given tail of a list
     *
     * @param head head to match
     * @param tail tail to match
     * @return new AnyList
     */
    static <T> AnyVal<Object> headTail(T head, ConsList<T> tail)
    {
        return new AnyConsImpl(head, null, tail, null);
    }

    /**
     * Return a new AnyList that matches the given head of a list and any tail of a list
     *
     * @param head head to match
     * @param tail holder for the value of the tail of the list that matches
     * @return new AnyList
     */
    static <T> AnyVal<Object> headAnyTail(T head, AnyVal<? extends List<? extends T>> tail)
    {
        return new AnyConsImpl(head, null, null, tail);
    }

    /**
     * Return a new AnyList that matches any head of a list and the given tail of a list
     *
     * @param head holder for the value of the head of the list that matches
     * @param tail tail to match
     * @return new AnyList
     */
    static <T> AnyVal<Object> anyHeadTail(AnyVal<T> head, ConsList<T> tail)
    {
        return new AnyConsImpl(null, head, tail, null);
    }

    /**
     * Return a new AnyList that matches any head of a list and any tail of a list
     *
     * @param head holder for the value of the head of the list that matches
     * @param tail holder for the value of the tail of the list that matches
     * @return new AnyList
     */
    static <T> AnyVal<Object> anyHeadAnyTail(AnyVal<T> head, AnyVal<? extends List<? extends T>> tail)
    {
        return new AnyConsImpl(null, head, null, tail);
    }

    /**
     * Return a new Any that matches any alias and will hold its value.
     *
     * @return Any for the given alias type
     */
    static <T extends REAL, REAL> AnyVal<T> typeAlias(TypeAliasType<REAL, T> typeAliasType)
    {
        return new AnyAlias<>(typeAliasType);
    }

    /**
     * Returns an Any that matches any null value
     *
     * @return Any for nulls
     */
    static AnyVal<?> anyNull()
    {
        return AnyNull.instance;
    }

    /**
     * Returns a new Any that matches a present Optional. The given value
     * is assigned the value of the optional on match.
     *
     * @param value will get the value of the optional
     * @return a new any
     */
    static <T> AnyVal<T> anySome(AnyVal<T> value)
    {
        return new AnyOptional<T>(value, null);
    }

    /**
     * Returns a new Any that holds an Optional. The given value
     * is assigned the optional itself on match.
     *
     * @param value will get the optional
     * @return a new any
     */
    static <T> AnyVal<T> anyOptional(AnyVal<Optional<T>> value)
    {
        return new AnyOptional<T>(null, value);
    }

    /**
     * Returns a new Any that matches an empty Optional.
     *
     * @return a new any
     */
    static AnyVal<Void> anyNone()
    {
        return new AnyOptional<Void>(null, null);
    }

    static <T> AnyVal<T> loose(AnyVal<T> any)
    {
        return any.loosely();
    }

    static <T> AnyVal<T> lit(T matchValue)
    {
        return new AnyVal<T>(matchValue, false, false){};
    }

    static <T> AnyVal<T> any()
    {
        return new AnyVal<T>(null, false, false){};
    }
}

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

/**
 * Value boxing any pattern matching mechanism
 */
public interface Any<T>
{
    /**
     * Return a new simple box for the given value. The value is initially set to <code>null</code>.
     *
     * @return new, null AnyVal
     */
    static <T> AnyVal<T> make()
    {
        return new AnyVal<>();
    }

    /**
     * Return a new AnyList that matches the given head of a list and the given tail of a list
     *
     * @param head head to match
     * @param tail tail to match
     * @return new AnyList
     */
    static <T> AnyList headTail(T head, ConsList<T> tail)
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
    static <T> AnyList headAnyTail(T head, Any<? extends ConsList<? extends T>> tail)
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
    static <T> AnyList anyHeadTail(Any<T> head, ConsList<T> tail)
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
    static <T> AnyList anyHeadAnyTail(Any<T> head, Any<? extends ConsList<? extends T>> tail)
    {
        return new AnyConsImpl(null, head, null, tail);
    }

    /**
     * Return a new Any that matches any alias and will hold its value.
     *
     * @return Any for the given alias type
     */
    static <T extends REAL, REAL> Any<T> typeAlias(TypeAliasType<REAL, T> typeAliasType)
    {
        return new AnyImpl<>(null, typeAliasType);
    }

    /**
     * Returns an Any that matches any null value
     *
     * @return Any for nulls
     */
    static AnyNull anyNull()
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
    static <T> AnyOptional<T> anySome(Any<T> value)
    {
        return new AnyOptional<T>(value){};
    }

    /**
     * Returns a new Any that matches an empty Optional.
     *
     * @return a new any
     */
    static AnyOptional<Void> anyNone()
    {
        return new AnyOptional<Void>(null){};
    }

    /**
     * @return the loaded value or <code>null</code>
     */
    T val();

    /**
     * @param value new value for the Any
     */
    void set(T value);

    /**
     * Used internally to determine if this Any can load/hold the given value
     *
     * @param value value to check
     * @return true/false
     */
    boolean canSet(T value);
}

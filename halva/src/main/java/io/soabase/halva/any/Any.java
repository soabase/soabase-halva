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

import io.soabase.halva.sugar.ConsList;

public interface Any<T>
{
    static <T> Any<Void> defineHeadTail(Object head, ConsList<T> tail)
    {
        return new AnyConsImpl<>(head, null, tail, null);
    }

    static <T> Any<Void> defineHeadAnyTail(Object head, Any<? extends ConsList<T>> tail)
    {
        return new AnyConsImpl<>(head, null, null, tail);
    }

    static <T> Any<Void> defineAnyHeadTail(Any<T> head, ConsList<?> tail)
    {
        return new AnyConsImpl<>(null, head, tail, null);
    }

    static <T> Any<Void> defineAnyHeadAnyTail(Any<T> head, Any<? extends ConsList<T>> tail)
    {
        return new AnyConsImpl<>(null, head, null, tail);
    }

    AnyDeclaration<T> getDeclaration();

    T val();

    boolean set(Object value);
}

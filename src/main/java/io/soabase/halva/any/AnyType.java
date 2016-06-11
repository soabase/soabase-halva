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

import io.soabase.com.google.inject.TypeLiteral;
import java.lang.reflect.Type;

public class AnyType<T> extends TypeLiteral<T>
{
    /**
     * Gets type literal for the given {@code Type} instance.
     */
    public static AnyType<?> get(Type type) {
        return new AnyType<>(type);
    }

    /**
     * Gets type literal for the given {@code Class} instance.
     */
    public static <T> AnyType<T> get(Class<T> type) {
        return new AnyType<>(type);
    }

    protected AnyType()
    {
    }

    private AnyType(Type type)
    {
        super(type);
    }
}

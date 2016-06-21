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

import io.soabase.com.google.inject.internal.MoreTypes;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static io.soabase.com.google.inject.internal.MoreTypes.canonicalize;

/**
 * A type token used to partially reify erased types
 */
public abstract class AnyType<T> extends AnyImpl<T, T>
{
    private final Class<? super T> rawType;

    protected AnyType()
    {
        this.rawType = getRawType(getClass());
    }

    @Override
    public final Class<? super T> getRawType()
    {
        return rawType;
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<? super T> getRawType(Class<? extends AnyType> superType)
    {
        Type superclass = superType.getGenericSuperclass();
        if ( superclass instanceof Class )
        {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        Type type = canonicalize(parameterized.getActualTypeArguments()[0]);
        return (Class<? super T>)MoreTypes.getRawType(type);
    }
}

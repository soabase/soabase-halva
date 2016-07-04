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

class InternalType
{
    final Type type;

    InternalType(Type type)
    {
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    static InternalType getInternalType(Class<?> superType, boolean throwIfMisspecified)
    {
        Type superclass = superType.getGenericSuperclass();
        if ( superclass instanceof Class )
        {
            if ( throwIfMisspecified )
            {
                throw new RuntimeException("Missing type parameter");
            }
            return new InternalType(superType);
        }
        ParameterizedType parameterized = (ParameterizedType) superclass;
        Type type = canonicalize(parameterized.getActualTypeArguments()[0]);
        if ( !MoreTypes.isFullySpecified(type) )
        {
            if ( throwIfMisspecified )
            {
                throw new RuntimeException("Parameterized type is not fully specified");
            }
            return new InternalType(superType);
        }
        return new InternalType(type);
    }

    @SuppressWarnings({"ConstantConditions", "unchecked"})
    boolean isAssignableFrom(InternalType from)
    {
        if ( MoreTypes.equals(type, from.type) )
        {
            return true;
        }

        //noinspection SimplifiableIfStatement
        if ( (type instanceof Class) && (from.type instanceof Class) )
        {
            return ((Class)type).isAssignableFrom((Class)from.type);
        }

        return false;
    }
}

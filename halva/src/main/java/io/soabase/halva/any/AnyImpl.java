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

class AnyImpl<T extends REAL, REAL> implements Any<T>
{
    private final TypeAliasType<REAL, T> typeAliasType;
    private T value;

    AnyImpl()
    {
        this(null);
    }

    AnyImpl(TypeAliasType<REAL, T> typeAliasType)
    {
        this.typeAliasType = typeAliasType;
    }

    @Override
    public final T val()
    {
        if ( value == null )
        {
            throw new IllegalArgumentException("No value set for: " + this);
        }
        return value;
    }

    @Override
    public final void set(T value)
    {
        if ( typeAliasType != null )
        {
            value = typeAliasType.wrap(value);
        }
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final boolean canSet(T value)
    {
        if ( value == null )
        {
            return true;
        }

        if ( typeAliasType != null )
        {
            return typeAliasType.getAliasType().canSet(typeAliasType.wrap(value));
        }

        return canSetExact(value, getInternalType());
    }

    static <T> boolean canSetExact(T value, AnyType.InternalType ourType)
    {
        if ( ourType != null )
        {
            try
            {
                AnyType.InternalType valueType = AnyType.getInternalType(value.getClass(), false);
                return ourType.isAssignableFrom(valueType);
            }
            catch ( ClassCastException dummy )
            {
                // dummy
            }
        }
        return false;
    }

    public AnyType.InternalType getInternalType()
    {
        return null;
    }
}

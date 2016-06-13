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
    private final Class<T> clazz;
    private final AnyType<T> typeLiteral;
    private final AnyDeclaration<T> declaration;
    private final TypeAliasType<REAL, T> typeAliasType;
    private volatile Object value = null;

    AnyImpl(AnyDeclaration<T> declaration, Class<T> clazz, AnyType<T> typeLiteral, TypeAliasType<REAL, T> typeAliasType)
    {
        this.declaration = declaration;
        this.typeAliasType = typeAliasType;
        if ( (clazz == null) && (typeLiteral == null) )
        {
            throw new IllegalArgumentException("clazz and typeLiteral cannot both be null");
        }

        this.clazz = clazz;
        this.typeLiteral = typeLiteral;
    }

    @Override
    public AnyDeclaration<T> getDeclaration()
    {
        return declaration;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T val()
    {
        if ( value == null )
        {
            throw new IllegalArgumentException("No value set for: " + this);
        }
        return (T)value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean set(Object value)
    {
        Class<? super T> castClass = getCastClass();
        if ( typeAliasType != null )
        {
            if ( typeAliasType.getRealType().getRawType().isAssignableFrom(castClass) )
            {
                try
                {
                    value = typeAliasType.wrap((T)value);
                }
                catch ( ClassCastException ignore )
                {
                    // nop
                }
            }
        }

        try
        {
            //noinspection unchecked
            this.value = castClass.cast(value);
            return true;
        }
        catch ( ClassCastException dummy )
        {
            // nop
        }
        return false;
    }

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();
        str.append((clazz != null) ? ("Any(" + clazz + ")") : ("Any(" + typeLiteral + ")"));
        if ( typeAliasType != null  )
        {
            str.append(" - alias for ").append(typeAliasType);
        }
        str.append(" value: ").append(value);
        return str.toString();
    }

    private Class<? super T> getCastClass()
    {
        return (clazz != null) ? clazz : typeLiteral.getRawType();
    }
}

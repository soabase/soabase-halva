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

public abstract class AnyVal<T>
{
    private final T matchValue;
    private T value;
    private final InternalType internalType;
    private final boolean isSettable;

    protected AnyVal()
    {
        this(null, true, true);
    }

    AnyVal(T matchValue, boolean isSettable, boolean throwIfMisspecified)
    {
        this.matchValue = matchValue;
        this.internalType = InternalType.getInternalType(getClass(), throwIfMisspecified);
        this.isSettable = isSettable;
    }

    public T val()
    {
        return value;
    }

    public void set(T value)
    {
        if ( matchValue == null )
        {
            this.value = value;
        }
        // else NOP
    }

    public boolean canSet(T value)
    {
        return isSettable ? canSetExact(value) : matches(value);
    }

    boolean canSet(T value, boolean exact)
    {
        if ( isSettable )
        {
            return exact ? canSetExact(value) : canSetLoose(value);
        }
        return matches(value);
    }

    boolean canSetLoose(T value)
    {
        if ( internalType != null )
        {
            try
            {
                internalType.rawType.cast(value);
                return true;
            }
            catch ( ClassCastException dummy )
            {
                // dummy
            }
            return false;
        }
        return false;
    }

    boolean canSetExact(T value)
    {
        if ( internalType != null )
        {
            try
            {
                InternalType valueType = InternalType.getInternalType(value.getClass(), false);
                return internalType.isAssignableFrom(valueType);
            }
            catch ( ClassCastException dummy )
            {
                // dummy
            }
        }
        return false;
    }

    private boolean matches(T value)
    {
        return (matchValue != null) && matchValue.equals(value);
    }
}

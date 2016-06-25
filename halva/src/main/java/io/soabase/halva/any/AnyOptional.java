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

import java.util.Optional;

/**
 * Represents portions of a list
 */
public abstract class AnyOptional<T> implements Any<T>
{
    private final Any holder;

    AnyOptional(Any holder)
    {
        this.holder = holder;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T val()
    {
        if ( holder == null )
        {
            throw new UnsupportedOperationException("Cannot get the value of an anyNone");
        }
        return (T)holder.val();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set(T value)
    {
        if ( (this.holder != null) && (value instanceof Optional) )
        {
            Optional optional = (Optional)value;
            if ( optional.isPresent() )
            {
                this.holder.set(optional.get());
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean canSet(T value)
    {
        if ( value instanceof Optional )
        {
            try
            {
                Optional optional = (Optional)value;
                if ( optional.isPresent() )
                {
                    //noinspection SimplifiableIfStatement
                    if ( holder == null )
                    {
                        return false;
                    }
                    return holder.canSet(optional.get());
                }
                return (this.holder == null);
            }
            catch ( ClassCastException dummy )
            {
                // dummy
            }
        }
        return false;
    }
}

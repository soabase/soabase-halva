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

class AnyOptional<T> extends AnyVal<T>
{
    private final AnyVal holder;
    private final AnyVal<? extends Optional> optionalHolder;

    AnyOptional(AnyVal holder, AnyVal<? extends Optional> optionalHolder)
    {
        super(null, true, false);
        this.holder = holder;
        this.optionalHolder = optionalHolder;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T val()
    {
        if ( holder != null )
        {
            return (T)holder.val();
        }

        if ( optionalHolder != null )
        {
            return (T)optionalHolder.val();
        }

        throw new UnsupportedOperationException("Cannot get the value of an anyNone");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set(T value)
    {
        if ( value instanceof Optional )
        {
            Optional optional = (Optional)value;
            if ( holder != null )
            {
                if ( optional.isPresent() )
                {
                    holder.set(optional.get());
                }
            }
            else if ( optionalHolder != null )
            {
                ((AnyVal<Optional>)optionalHolder).set(optional);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean canSet(T value)
    {
        if ( value instanceof Optional )
        {
            if ( optionalHolder != null )
            {
                return true;
            }

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
                    return holder.canSetExact(optional.get());
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

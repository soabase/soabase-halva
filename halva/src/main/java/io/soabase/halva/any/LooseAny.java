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

class LooseAny<T> extends AnyVal<T>
{
    private final AnyVal<T> val;

    LooseAny(AnyVal<T> val)
    {
        super(null, true, false);
        this.val = val;
    }

    @Override
    public T val()
    {
        return val.val();
    }

    @Override
    public void set(T value)
    {
        val.set(value);
    }

    @Override
    public boolean canSet(T value)
    {
        return val.canSet(value, false);
    }

    @Override
    boolean canSetLoose(T value)
    {
        return val.canSet(value, false);
    }

    @Override
    boolean canSetExact(T value)
    {
        throw new UnsupportedOperationException();
    }
}

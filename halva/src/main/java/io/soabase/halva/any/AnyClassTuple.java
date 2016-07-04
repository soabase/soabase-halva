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

import io.soabase.halva.tuple.ClassTuple;
import io.soabase.halva.tuple.Tuple;

public abstract class AnyClassTuple<T> extends AnyVal<T> implements ClassTuple
{
    private final Tuple tuple;

    protected AnyClassTuple(Tuple tuple)
    {
        super(null, true, false);
        this.tuple = tuple;
    }

    @Override
    public boolean canSet(T value)
    {
        return (value instanceof ClassTuple) && super.canSet(value);
    }

    @Override
    public final Tuple tuple()
    {
        return tuple;
    }

    @Override
    AnyVal<T> loosely()
    {
        return this;
    }
}

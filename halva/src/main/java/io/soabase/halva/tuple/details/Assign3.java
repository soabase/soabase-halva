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
package io.soabase.halva.tuple.details;

import io.soabase.halva.any.AnyVal;

public class Assign3<A, B, C>
{
    public final AnyVal<A> _1;
    public final AnyVal<B> _2;
    public final AnyVal<C> _3;

    public Assign3(AnyVal<A> _1, AnyVal<B> _2, AnyVal<C> _3)
    {
        this._1 = _1;
        this._2 = _2;
        this._3 = _3;
    }

    public Tuple3<A, B, C> from(Tuple3<A, B, C> from)
    {
        _1.set(from._1);
        _2.set(from._2);
        _3.set(from._3);
        return from;
    }
}

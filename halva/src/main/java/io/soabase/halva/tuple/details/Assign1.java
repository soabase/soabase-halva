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

public class Assign1<A>
{
    public final AnyVal<A> _1;

    public Assign1(AnyVal<A> _1)
    {
        this._1 = _1;
    }

    public Tuple1<A> from(Tuple1<A> from)
    {
        _1.set(from._1);
        return from;
    }
}

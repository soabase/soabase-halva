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

public class Tuple4<A, B, C, D> extends TupleImpl
{
    public final A _1;
    public final B _2;
    public final C _3;
    public final D _4;

    public Tuple4(A _1, B _2, C _3, D _4)
    {
        super(_1, _2, _3, _4);
        this._1 = _1;
        this._2 = _2;
        this._3 = _3;
        this._4 = _4;
    }
}

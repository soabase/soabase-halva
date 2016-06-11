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

public class Tuple2<A, B> extends TupleImpl
{
    public final A _1;
    public final B _2;

    public Tuple2(A _1, B _2)
    {
        super(_1, _2);
        this._1 = _1;
        this._2 = _2;
    }

    public Tuple2<B, A> swap()
    {
        return new Tuple2<>(_2, _1);
    }
}

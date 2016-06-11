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
package io.soabase.halva.tuple;

import io.soabase.halva.tuple.details.*;
import java.util.Collection;
import java.util.Optional;

public interface Tuple extends Collection<Object>
{
    static Tuple0 T()
    {
        return new Tuple0();
    }
    static <A> Tuple1<A> T(A _1)
    {
        return new Tuple1<>(_1);
    }
    static <A, B> Tuple2<A, B> T(A _1, B _2)
    {
        return new Tuple2<>(_1, _2);
    }
    static <A, B, C> Tuple3<A, B, C> T(A _1, B _2, C _3)
    {
        return new Tuple3<>(_1, _2, _3);
    }
    static <A, B, C, D> Tuple4<A, B, C, D> T(A _1, B _2, C _3, D _4)
    {
        return new Tuple4<>(_1, _2, _3, _4);
    }
    static <A, B, C, D, E> Tuple5<A, B, C, D, E> T(A _1, B _2, C _3, D _4, E _5)
    {
        return new Tuple5<>(_1, _2, _3, _4, _5);
    }
    static <A, B, C, D, E, F> Tuple6<A, B, C, D, E, F> T(A _1, B _2, C _3, D _4, E _5, F _6)
    {
        return new Tuple6<>(_1, _2, _3, _4, _5, _6);
    }
    static <A, B, C, D, E, F, G> Tuple7<A, B, C, D, E, F, G> T(A _1, B _2, C _3, D _4, E _5, F _6, G _7)
    {
        return new Tuple7<>(_1, _2, _3, _4, _5, _6, _7);
    }
    static <A, B, C, D, E, F, G, H> Tuple8<A, B, C, D, E, F, G, H> T(A _1, B _2, C _3, D _4, E _5, F _6, G _7, H _8)
    {
        return new Tuple8<>(_1, _2, _3, _4, _5, _6, _7, _8);
    }
    static <A, B, C, D, E, F, G, H, I> Tuple9<A, B, C, D, E, F, G, H, I> T(A _1, B _2, C _3, D _4, E _5, F _6, G _7, H _8, I _9)
    {
        return new Tuple9<>(_1, _2, _3, _4, _5, _6, _7, _8, _9);
    }
    static <A, B, C, D, E, F, G, H, I, J> Tuple10<A, B, C, D, E, F, G, H, I, J> T(A _1, B _2, C _3, D _4, E _5, F _6, G _7, H _8, I _9, J _10)
    {
        return new Tuple10<>(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10);
    }
    static <A, B, C, D, E, F, G, H, I, J, K> Tuple11<A, B, C, D, E, F, G, H, I, J, K> T(A _1, B _2, C _3, D _4, E _5, F _6, G _7, H _8, I _9, J _10, K _11)
    {
        return new Tuple11<>(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11);
    }
    static <A, B, C, D, E, F, G, H, I, J, K, L> Tuple12<A, B, C, D, E, F, G, H, I, J, K, L> T(A _1, B _2, C _3, D _4, E _5, F _6, G _7, H _8, I _9, J _10, K _11, L _12)
    {
        return new Tuple12<>(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12);
    }
    static <A, B, C, D, E, F, G, H, I, J, K, L, M> Tuple13<A, B, C, D, E, F, G, H, I, J, K, L, M> T(A _1, B _2, C _3, D _4, E _5, F _6, G _7, H _8, I _9, J _10, K _11, L _12, M _13)
    {
        return new Tuple13<>(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13);
    }
    static <A, B, C, D, E, F, G, H, I, J, K, L, M, N> Tuple14<A, B, C, D, E, F, G, H, I, J, K, L, M, N> T(A _1, B _2, C _3, D _4, E _5, F _6, G _7, H _8, I _9, J _10, K _11, L _12, M _13, N _14)
    {
        return new Tuple14<>(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14);
    }
    static <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> Tuple15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> T(A _1, B _2, C _3, D _4, E _5, F _6, G _7, H _8, I _9, J _10, K _11, L _12, M _13, N _14, O _15)
    {
        return new Tuple15<>(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15);
    }
    static <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> Tuple16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> T(A _1, B _2, C _3, D _4, E _5, F _6, G _7, H _8, I _9, J _10, K _11, L _12, M _13, N _14, O _15, P _16)
    {
        return new Tuple16<>(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16);
    }

    static <A, B> Pair<A, B> Pair(A _1, B _2)
    {
        return new Pair<>(_1, _2);
    }

    @SuppressWarnings("unchecked")
    static Optional<Class<? extends Tuple>> getTupleClass(int argQty)
    {
        try
        {
            Class<? extends Tuple> clazz = (Class<? extends Tuple>)Class.forName("io.soabase.halva.tuple.details.Tuple" + argQty);
            return Optional.of(clazz);
        }
        catch ( ClassNotFoundException ignore )
        {
            // ignore
        }
        return Optional.empty();
    }

    boolean extract(Object o);
}

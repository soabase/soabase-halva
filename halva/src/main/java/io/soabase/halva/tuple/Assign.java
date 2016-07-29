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

import io.soabase.halva.any.AnyVal;
import io.soabase.halva.tuple.details.*;

/**
 * Main factory for creating Assigns
 */
@SuppressWarnings("MethodNameSameAsClassName")
public interface Assign
{
    static <A> Assign1<A> Assign(AnyVal<A> _1)
    {
        return new Assign1<>(_1);
    }
    static <A, B> Assign2<A, B> Assign(AnyVal<A> _1, AnyVal<B> _2)
    {
        return new Assign2<>(_1, _2);
    }
    static <A, B, C> Assign3<A, B, C> Assign(AnyVal<A> _1, AnyVal<B> _2, AnyVal<C> _3)
    {
        return new Assign3<>(_1, _2, _3);
    }
    static <A, B, C, D> Assign4<A, B, C, D> Assign(AnyVal<A> _1, AnyVal<B> _2, AnyVal<C> _3, AnyVal<D> _4)
    {
        return new Assign4<>(_1, _2, _3, _4);
    }
    static <A, B, C, D, E> Assign5<A, B, C, D, E> Assign(AnyVal<A> _1, AnyVal<B> _2, AnyVal<C> _3, AnyVal<D> _4, AnyVal<E> _5)
    {
        return new Assign5<>(_1, _2, _3, _4, _5);
    }
    static <A, B, C, D, E, F> Assign6<A, B, C, D, E, F> Assign(AnyVal<A> _1, AnyVal<B> _2, AnyVal<C> _3, AnyVal<D> _4, AnyVal<E> _5, AnyVal<F> _6)
    {
        return new Assign6<>(_1, _2, _3, _4, _5, _6);
    }
    static <A, B, C, D, E, F, G> Assign7<A, B, C, D, E, F, G> Assign(AnyVal<A> _1, AnyVal<B> _2, AnyVal<C> _3, AnyVal<D> _4, AnyVal<E> _5, AnyVal<F> _6, AnyVal<G> _7)
    {
        return new Assign7<>(_1, _2, _3, _4, _5, _6, _7);
    }
    static <A, B, C, D, E, F, G, H> Assign8<A, B, C, D, E, F, G, H> Assign(AnyVal<A> _1, AnyVal<B> _2, AnyVal<C> _3, AnyVal<D> _4, AnyVal<E> _5, AnyVal<F> _6, AnyVal<G> _7, AnyVal<H> _8)
    {
        return new Assign8<>(_1, _2, _3, _4, _5, _6, _7, _8);
    }
    static <A, B, C, D, E, F, G, H, I> Assign9<A, B, C, D, E, F, G, H, I> Assign(AnyVal<A> _1, AnyVal<B> _2, AnyVal<C> _3, AnyVal<D> _4, AnyVal<E> _5, AnyVal<F> _6, AnyVal<G> _7, AnyVal<H> _8, AnyVal<I> _9)
    {
        return new Assign9<>(_1, _2, _3, _4, _5, _6, _7, _8, _9);
    }
    static <A, B, C, D, E, F, G, H, I, J> Assign10<A, B, C, D, E, F, G, H, I, J> Assign(AnyVal<A> _1, AnyVal<B> _2, AnyVal<C> _3, AnyVal<D> _4, AnyVal<E> _5, AnyVal<F> _6, AnyVal<G> _7, AnyVal<H> _8, AnyVal<I> _9, AnyVal<J> _10)
    {
        return new Assign10<>(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10);
    }
    static <A, B, C, D, E, F, G, H, I, J, K> Assign11<A, B, C, D, E, F, G, H, I, J, K> Assign(AnyVal<A> _1, AnyVal<B> _2, AnyVal<C> _3, AnyVal<D> _4, AnyVal<E> _5, AnyVal<F> _6, AnyVal<G> _7, AnyVal<H> _8, AnyVal<I> _9, AnyVal<J> _10, AnyVal<K> _11)
    {
        return new Assign11<>(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11);
    }
    static <A, B, C, D, E, F, G, H, I, J, K, L> Assign12<A, B, C, D, E, F, G, H, I, J, K, L> Assign(AnyVal<A> _1, AnyVal<B> _2, AnyVal<C> _3, AnyVal<D> _4, AnyVal<E> _5, AnyVal<F> _6, AnyVal<G> _7, AnyVal<H> _8, AnyVal<I> _9, AnyVal<J> _10, AnyVal<K> _11, AnyVal<L> _12)
    {
        return new Assign12<>(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12);
    }
    static <A, B, C, D, E, F, G, H, I, J, K, L, M> Assign13<A, B, C, D, E, F, G, H, I, J, K, L, M> Assign(AnyVal<A> _1, AnyVal<B> _2, AnyVal<C> _3, AnyVal<D> _4, AnyVal<E> _5, AnyVal<F> _6, AnyVal<G> _7, AnyVal<H> _8, AnyVal<I> _9, AnyVal<J> _10, AnyVal<K> _11, AnyVal<L> _12, AnyVal<M> _13)
    {
        return new Assign13<>(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13);
    }
    static <A, B, C, D, E, F, G, H, I, J, K, L, M, N> Assign14<A, B, C, D, E, F, G, H, I, J, K, L, M, N> Assign(AnyVal<A> _1, AnyVal<B> _2, AnyVal<C> _3, AnyVal<D> _4, AnyVal<E> _5, AnyVal<F> _6, AnyVal<G> _7, AnyVal<H> _8, AnyVal<I> _9, AnyVal<J> _10, AnyVal<K> _11, AnyVal<L> _12, AnyVal<M> _13, AnyVal<N> _14)
    {
        return new Assign14<>(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14);
    }
    static <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> Assign15<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O> Assign(AnyVal<A> _1, AnyVal<B> _2, AnyVal<C> _3, AnyVal<D> _4, AnyVal<E> _5, AnyVal<F> _6, AnyVal<G> _7, AnyVal<H> _8, AnyVal<I> _9, AnyVal<J> _10, AnyVal<K> _11, AnyVal<L> _12, AnyVal<M> _13, AnyVal<N> _14, AnyVal<O> _15)
    {
        return new Assign15<>(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15);
    }
    static <A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> Assign16<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P> Assign(AnyVal<A> _1, AnyVal<B> _2, AnyVal<C> _3, AnyVal<D> _4, AnyVal<E> _5, AnyVal<F> _6, AnyVal<G> _7, AnyVal<H> _8, AnyVal<I> _9, AnyVal<J> _10, AnyVal<K> _11, AnyVal<L> _12, AnyVal<M> _13, AnyVal<N> _14, AnyVal<O> _15, AnyVal<P> _16)
    {
        return new Assign16<>(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12, _13, _14, _15, _16);
    }
}

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
package io.soabase.halva.sugar;

import io.soabase.halva.tuple.details.Tuple2;
import java.util.*;

/**
 * Some conveniences for making immutable collections
 */
public class Sugar
{
    /**
     * Same as calling <code>list.cons(newHead)</code>
     *
     * @param newHead new head
     * @param list list
     * @return new list
     */
    public static <T> ConsList<T> cons(T newHead, ConsList<T> list)
    {
        return list.cons(newHead);
    }

    /**
     * Same as calling <code>lhs.concat(rhs)</code>
     *
     * @param lhs left-hand list
     * @param rhs right-hand list
     * @return new list
     */
    public static <T> ConsList<T> concat(ConsList<T> lhs, ConsList<T> rhs)
    {
        return lhs.concat(rhs);
    }

    /**
     * Return a new ConsList consisting of the given items
     *
     * @param a items
     * @return new cons list
     */
    @SafeVarargs
    public static <T> ConsList<T> List(T... a)
    {
        if ( (a == null) || (a.length == 0) )
        {
            return new ConsListImpl<>();
        }
        return new ConsListImpl<>(Arrays.asList(a), false);
    }

    /**
     * Return a new immutable set consisting of the given item
     *
     * @param a items
     * @return a new set
     */
    @SafeVarargs
    public static <T> Set<T> Set(T... a)
    {
        if ( (a == null) || (a.length == 0) )
        {
            return Collections.unmodifiableSet(new HashSet<>());
        }
        Set<T> set = new HashSet<>(a.length);
        Collections.addAll(set, a);
        return Collections.unmodifiableSet(set);
    }

    /**
     * Return a new immutable map consisting of keys,values using
     * the given pairs
     *
     * @param kvs key/value pairs
     * @return a new map
     */
    @SafeVarargs
    public static <K, V> Map<K, V> Map(Tuple2<K, V>... kvs)
    {
        if ( (kvs == null) || (kvs.length == 0) )
        {
            return Collections.unmodifiableMap(new HashMap<>());
        }
        Map<K, V> map = new HashMap<>(kvs.length);
        for ( Tuple2<K, V> t : kvs )
        {
            map.put(t._1, t._2);
        }
        return Collections.unmodifiableMap(map);
    }

    /**
     * Return a new empty iterator
     *
     * @return iterator
     */
    public static <T> Iterator<T> Iterator()
    {
        return new Iterator<T>()
        {
            @Override
            public boolean hasNext()
            {
                return false;
            }

            @Override
            public T next()
            {
                throw new NoSuchElementException();
            }
        };
    }

    /**
     * Return a new iterator over a single item
     *
     * @param object single object to be "iterated" over
     * @return iterator
     */
    public static <T> Iterator<T> Iterator(T object)
    {
        return new Iterator<T>()
        {
            private boolean hasNext = true;

            @Override
            public boolean hasNext()
            {
                return hasNext;
            }

            @Override
            public T next()
            {
                if ( !hasNext )
                {
                    throw new NoSuchElementException();
                }
                hasNext = false;
                return object;
            }
        };
    }

    /**
     * Return a new empty iterable
     *
     * @return iterable
     */
    public static <T> Iterable<T> Iterable()
    {
        return Sugar::Iterator;
    }

    /**
     * Return a new iterable over a single item
     *
     * @param object single object to be "iterated" over
     * @return iterable
     */
    public static <T> Iterable<T> Iterable(T object)
    {
        return () -> Iterator(object);
    }

    private Sugar()
    {
    }
}

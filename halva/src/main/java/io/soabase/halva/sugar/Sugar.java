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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

// http://www.grahamlea.com/2014/08/java-8-lambdas-scala-obsolete/

public class Sugar
{
    public static <T> ConsList<T> cons(T newHead, ConsList<T> list)
    {
        return list.cons(newHead);
    }

    public static <T> ConsList<T> concat(ConsList<T> lhs, ConsList<T> rhs)
    {
        return lhs.concat(rhs);
    }

    @SafeVarargs
    public static <T> ConsList<T> List(T... a)
    {
        if ( (a == null) || (a.length == 0) )
        {
            return new ConsListImpl<>();
        }
        return new ConsListImpl<>(Arrays.asList(a));
    }

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

    @SafeVarargs
    public static <K, V> Map<K, V> Map(Tuple2<K, V>... kvs)
    {
        if ( (kvs == null) || (kvs.length == 0) )
        {
            return Collections.unmodifiableMap(new HashMap<>());
        }
        HashMap<K, V> map = new HashMap<>(kvs.length);
        for ( Tuple2<K, V> t : kvs )
        {
            map.put(t._1, t._2);
        }
        return Collections.unmodifiableMap(map);
    }

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

    public static <T> Iterable<T> Iterable(T object)
    {
        return () -> Iterator(object);
    }

    private Sugar()
    {
    }
}

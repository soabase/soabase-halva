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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

class ConsListImpl<T> implements ConsList<T>
{
    private final List<T> list;

    ConsListImpl()
    {
        this(Collections.unmodifiableList(new ArrayList<>()), null);
    }

    ConsListImpl(List<T> list)
    {
        this(wrapList(list), null);
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> wrapList(List<T> list)
    {
        if ( list instanceof ConsListImpl )
        {
            return ((ConsListImpl)list).list;
        }
        return Collections.unmodifiableList(new ArrayList<>(list));
    }

    ConsListImpl(Iterator<T> list)
    {
        this(null, list);
    }

    private ConsListImpl(List<T> list, Iterator<T> iterator)
    {
        if ( list == null )
        {
            if ( iterator == null )
            {
                throw new IllegalArgumentException("both list and iterator cannot be null");
            }
            List<T> worker = new ArrayList<>();
            while ( iterator.hasNext() )
            {
                worker.add(iterator.next());
            }
            list = Collections.unmodifiableList(new ArrayList<>(worker));
        }
        this.list = list;
    }

    @Override
    public ConsList<T> concat(ConsList<T> rhs)
    {
        ArrayList<T> worker = new ArrayList<>(list);
        worker.addAll(rhs);
        return new ConsListImpl<>(worker, null);
    }

    @Override
    public ConsList<T> cons(T newHead)
    {
        ArrayList<T> worker = new ArrayList<>();
        worker.add(newHead);
        worker.addAll(list);
        return new ConsListImpl<>(worker, null);
    }

    @Override
    public String toString()
    {
        return list.toString();
    }

    @Override
    public T head()
    {
        return get(0);
    }

    @Override
    public ConsList<T> tail()
    {
        return new ConsListImpl<>(list.subList(1, size()), null);
    }

    @Override
    public int size()
    {
        return list.size();
    }

    @Override
    public boolean isEmpty()
    {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o)
    {
        return list.contains(o);
    }

    @Override
    public Iterator<T> iterator()
    {
        Iterator<T> iterator = list.iterator();
        return new Iterator<T>()
        {
            @Override
            public boolean hasNext()
            {
                return iterator.hasNext();
            }

            @Override
            public T next()
            {
                return iterator.next();
            }
        };
    }

    @Override
    public Object[] toArray()
    {
        return list.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a)
    {
        //noinspection SuspiciousToArrayCall,SuspiciousToArrayCall
        return list.toArray(a);
    }

    @Override
    public boolean add(T t)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c)
    {
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void replaceAll(UnaryOperator<T> operator)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sort(Comparator<? super T> c)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear()
    {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o)
    {
        return (this == o) || list.equals(o);
    }

    @Override
    public int hashCode()
    {
        return list.hashCode();
    }

    @Override
    public T get(int index)
    {
        return list.get(index);
    }

    @Override
    public T set(int index, T element)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, T element)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int index)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o)
    {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o)
    {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator()
    {
        return listIterator(0);
    }

    @Override
    public ListIterator<T> listIterator(int index)
    {
        ListIterator<T> iterator = list.listIterator(index);
        return new ListIterator<T>()
        {
            @Override
            public boolean hasNext()
            {
                return iterator.hasNext();
            }

            @Override
            public T next()
            {
                return iterator.next();
            }

            @Override
            public boolean hasPrevious()
            {
                return iterator.hasPrevious();
            }

            @Override
            public T previous()
            {
                return iterator.previous();
            }

            @Override
            public int nextIndex()
            {
                return iterator.nextIndex();
            }

            @Override
            public int previousIndex()
            {
                return iterator.previousIndex();
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }

            @Override
            public void set(T t)
            {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(T t)
            {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex)
    {
        return new ConsListImpl<>(list.subList(fromIndex, toIndex));
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Stream<T> stream()
    {
        return list.stream();
    }

    @Override
    public Stream<T> parallelStream()
    {
        return list.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super T> action)
    {
        list.forEach(action);
    }
}

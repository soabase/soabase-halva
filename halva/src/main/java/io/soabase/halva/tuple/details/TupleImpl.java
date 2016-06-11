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

import io.soabase.halva.any.Any;
import io.soabase.halva.tuple.ClassTuplable;
import io.soabase.halva.tuple.ClassTuple;
import io.soabase.halva.tuple.Tuplable;
import io.soabase.halva.tuple.Tuple;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

abstract class TupleImpl implements Tuple
{
    private final List<Object> items;

    protected TupleImpl(Object... items)
    {
        this.items = ((items == null) || (items.length == 0)) ? new ArrayList<>() : Collections.unmodifiableList(Arrays.asList(items));
    }

    @Override
    public Iterator<Object> iterator()
    {
        Iterator<Object> iterator = items.iterator();
        return new Iterator<Object>()
        {
            @Override
            public boolean hasNext()
            {
                return iterator.hasNext();
            }

            @Override
            public Object next()
            {
                return iterator.next();
            }
        };
    }

    @Override
    public boolean extract(Object o)
    {
        return internalExtract(o, true);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o)
    {
        return internalExtract(o, false);
    }

    @Override
    public int hashCode()
    {
        return items.hashCode();
    }

    @Override
    public String toString()
    {
        return items.stream()
            .map(item -> item instanceof String ? ("\"" + item + "\"") : String.valueOf(item))
            .collect(Collectors.joining(", ", "(", ")"));
    }

    @Override
    public int size()
    {
        return items.size();
    }

    @Override
    public boolean isEmpty()
    {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o)
    {
        return items.contains(o);
    }

    @Override
    public Object[] toArray()
    {
        return items.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
        //noinspection ConstantConditions,SuspiciousToArrayCall
        return items.toArray(a);
    }

    @Override
    public boolean add(Object o)
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
        return items.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<?> c)
    {
        throw new UnsupportedOperationException();
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
    public void clear()
    {
        throw new UnsupportedOperationException();
    }

    private boolean internalExtract(Object o, boolean processPredicates)
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null )
        {
            return false;
        }

        if ( getClass() == o.getClass() )
        {
            TupleImpl objects = (TupleImpl)o;
            return hasEqualItems(objects.items, processPredicates);
        }

        // special case class tuples
        if ( items.size() == 1 )
        {
            Object t = items.get(0);
            if ( isClassTuple(t, o) )
            {
                return checkClassTuple(t, o, processPredicates);
            }
        }

        //noinspection SimplifiableIfStatement
        if ( Tuplable.class.isAssignableFrom(o.getClass()) )
        {
            return internalExtract(((Tuplable)o).tuple(), processPredicates);
        }

        return hasEqualItems(Collections.singletonList(o), processPredicates);
    }

    private boolean isClassTuple(Object t, Object o)
    {
        return (t instanceof ClassTuple) && (o instanceof ClassTuplable);
    }

    private boolean checkClassTuple(Object t, Object o, boolean processPredicates)
    {
        if ( !t.getClass().isAssignableFrom(((ClassTuplable)o).getClassTuplableClass()) )
        {
            return false;
        }

        Tuple resolved = ((ClassTuple)t).tuple();
        return (resolved instanceof TupleImpl) && ((TupleImpl)resolved).internalExtract(o, processPredicates);
    }

    @SuppressWarnings("unchecked")
    private boolean hasEqualItems(List<Object> rhsItems, boolean processPredicates)
    {
        if ( items.size() != rhsItems.size() )
        {
            return false;
        }
        for ( int i = 0; i < items.size(); ++i )
        {
            Object lhs = items.get(i);
            Object rhs = rhsItems.get(i);

            if ( rhs instanceof Any )
            {
                if ( !((Any)rhs).set(lhs) )
                {
                    return false;
                }
                continue;
            }
            if ( lhs instanceof Any )
            {
                if ( !((Any)lhs).set(rhs) )
                {
                    return false;
                }
                continue;
            }

            if ( lhs instanceof TupleImpl )
            {
                if ( !((TupleImpl)lhs).internalExtract(rhs, processPredicates) )
                {
                    return false;
                }
                continue;
            }

            if ( rhs instanceof TupleImpl )
            {
                if ( !((TupleImpl)rhs).internalExtract(lhs, processPredicates) )
                {
                    return false;
                }
                continue;
            }

            if ( processPredicates && (lhs instanceof Predicate) )
            {
                if ( !((Predicate)lhs).test(rhs) )
                {
                    return false;
                }
                continue;
            }

            if ( isClassTuple(lhs, rhs) )
            {
                if ( !checkClassTuple(lhs, rhs, processPredicates) )
                {
                    return false;
                }
                continue;
            }

            if ( !lhs.equals(rhs) )
            {
                return false;
            }
        }
        return true;
    }
}

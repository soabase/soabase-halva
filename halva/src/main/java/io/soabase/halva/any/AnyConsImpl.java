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
package io.soabase.halva.any;

import io.soabase.halva.sugar.ConsList;

class AnyConsImpl<T> implements Any<Void>
{
    private final Object head;
    private final Any<T> anyHead;
    private final ConsList<?> tail;
    private final Any<? extends ConsList<T>> anyTail;

    AnyConsImpl(Object head, Any<T> anyHead, ConsList<?> tail, Any<? extends ConsList<T>> anyTail)
    {
        this.head = head;
        this.anyHead = anyHead;
        this.tail = tail;
        this.anyTail = anyTail;
    }

    @Override
    public AnyDeclaration<Void> getDeclaration()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Void val()
    {
        return null;
    }

    @Override
    public String toString()
    {
        return "AnyConsImpl{" +
            "head=" + head +
            ", anyHead=" + anyHead +
            ", tail=" + tail +
            ", anyTail=" + anyTail +
            '}';
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean set(Object value)
    {
        //noinspection LoopStatementThatDoesntLoop
        while ( value instanceof ConsList )
        {
            ConsList list = (ConsList)value;
            if ( list.size() == 0 )
            {
                throw new IllegalArgumentException("list is empty");
            }
            Object listHead = list.head();
            ConsList<Object> listTail = list.tail();
            if ( (head != null) && !head.equals(listHead) )
            {
                break;
            }
            if ( (anyHead != null) && !anyHead.set(listHead) )
            {
                break;
            }
            if ( (tail != null) && !tail.equals(listTail) )
            {
                break;
            }
            if ( (anyTail != null) && !anyTail.set(listTail) )
            {
                break;
            }

            return true;
        }
        return false;
    }
}

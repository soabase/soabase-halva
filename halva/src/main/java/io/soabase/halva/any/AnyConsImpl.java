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

class AnyConsImpl extends AnyVal<Object>
{
    private final Object head;
    private final AnyVal anyHead;
    private final ConsList tail;
    private final AnyVal anyTail;

    @SuppressWarnings("unchecked")
    AnyConsImpl(Object head, AnyVal anyHead, ConsList tail, AnyVal anyTail)
    {
        super(null, true, false);
        this.head = head;
        this.anyHead = (anyHead != null) ? Any.loose(anyHead) : null;
        this.tail = tail;
        this.anyTail = (anyTail != null) ? Any.loose(anyTail) : null;
    }

    @Override
    public Object val()
    {
        return null;
    }

    @Override
    public String toString()
    {
        return "BoxConsImpl{" +
            "head=" + head +
            ", anyHead=" + anyHead +
            ", tail=" + tail +
            ", anyTail=" + anyTail +
            '}';
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean canSet(Object value)
    {
        //noinspection LoopStatementThatDoesntLoop
        while ( value instanceof ConsList )
        {
            ConsList<Object> list = (ConsList)value;
            if ( list.size() == 0 )
            {
                throw new IllegalArgumentException("list is empty");
            }
            Object listHead = list.head();
            ConsList<Object> listTail =  list.tail();
            if ( (head != null) && !head.equals(listHead) )
            {
                break;
            }
            if ( (anyHead != null) && !anyHead.canSet(listHead) )
            {
                break;
            }
            if ( (tail != null) && !tail.equals(listTail) )
            {
                break;
            }
            if ( (anyTail != null) && !anyTail.canSet(listTail) )
            {
                break;
            }

            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void set(Object value)
    {
        if ( value instanceof ConsList )
        {
            ConsList<Object> list = (ConsList)value;
            Object listHead = list.head();
            ConsList<Object> listTail = list.tail();
            if ( anyHead != null )
            {
                anyHead.set(listHead);
            }
            if ( anyTail != null )
            {
                anyTail.set(listTail);
            }
        }
    }
}

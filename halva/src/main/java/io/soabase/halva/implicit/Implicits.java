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
package io.soabase.halva.implicit;

import io.soabase.halva.any.AnyDeclaration;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.Supplier;

public class Implicits
{
    // http://baddotrobot.com/blog/2015/07/03/scala-implicit-parameters/
    // http://docs.scala-lang.org/tutorials/FAQ/finding-implicits.html

    private static volatile Implicits implicits = new Implicits();
    private final ImplicitContext context = new ImplicitContext();
    private final ReadWriteLock lock = new ReentrantReadWriteLock(true);

    public static void debugClearAll()
    {
        implicits = new Implicits();
    }

    @SuppressWarnings("MethodNameSameAsClassName")
    public static Implicits Implicits()
    {
        return implicits;
    }

    public void push()
    {
        Lock lock = this.lock.writeLock();
        lock.lock();
        try
        {
            if ( !context.push() )
            {
                throw new IllegalStateException("Pushing would overflow internal buffer");
            }
        }
        finally
        {
            lock.unlock();
        }
    }

    public synchronized void pop()
    {
        Lock lock = this.lock.writeLock();
        lock.lock();
        try
        {
            if ( !context.pop() )
            {
                throw new IllegalStateException("This implicit container is at the root and cannot be popped");
            }
        }
        finally
        {
            lock.unlock();
        }
    }

    public <FROM, TO> void setConversion(AnyDeclaration<FROM> fromType, AnyDeclaration<TO> toType, Function<FROM, TO> converter)
    {
        if ( converter == null )
        {
            throw new IllegalArgumentException("converter cannot be null");
        }
        executeRead(() -> context.setConversion(converter, fromType, toType));
    }

    public <T> void setValue(AnyDeclaration<T> key, T value)
    {
        if ( value == null )
        {
            throw new IllegalArgumentException("value cannot be null");
        }
        setValueProvider(key, () -> value);
    }

    public <T> void setValueProvider(AnyDeclaration<T> key, Supplier<T> valueSupplier)
    {
        if ( key == null )
        {
            throw new IllegalArgumentException("key cannot be null");
        }
        if ( valueSupplier == null )
        {
            throw new IllegalArgumentException("valueSupplier cannot be null");
        }
        executeRead(() -> context.set(key, valueSupplier));
    }

    public <T> T getValue(AnyDeclaration<T> any)
    {
        if ( any == null )
        {
            throw new IllegalArgumentException("any cannot be null");
        }
        return internalGetValue(any, null);
    }

    public <T> T getValue(AnyDeclaration<T> any, T defaultValue)
    {
        if ( any == null )
        {
            throw new IllegalArgumentException("any cannot be null");
        }
        if ( defaultValue == null )
        {
            throw new IllegalArgumentException("defaultValue cannot be null");
        }
        return internalGetValue(any, defaultValue);
    }

    private <T> T internalGetValue(AnyDeclaration<T> any, T defaultValue)
    {
        return executeRead(() -> {
            T value = context.get(any, defaultValue);
            if ( value == null )
            {
                throw new IllegalArgumentException("No implicit value found and no default provided for: " + any);
            }
            return value;
        });
    }

    private void executeRead(Runnable proc)
    {
        executeRead(() -> {
            proc.run();
            return null;
        });
    }

    private <T> T executeRead(Supplier<T> proc)
    {
        Lock lock = this.lock.readLock();
        lock.lock();
        try
        {
            return proc.get();
        }
        finally
        {
            lock.unlock();
        }
    }

    private Implicits()
    {
        push();
    }
}

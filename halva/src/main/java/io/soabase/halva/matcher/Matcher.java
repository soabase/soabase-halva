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
package io.soabase.halva.matcher;

import io.soabase.halva.any.Any;
import io.soabase.halva.any.AnyList;
import io.soabase.halva.any.AnyNull;
import io.soabase.halva.any.AnyOptional;
import io.soabase.halva.any.AnyType;
import io.soabase.halva.sugar.ConsList;
import io.soabase.halva.tuple.Tuple;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Main factory for pattern matching
 */
public class Matcher<ARG> extends Getter<ARG> implements CasesBase<ARG, Matcher<ARG>>
{
    private Any nextBinder = null;

    /**
     * Start a pattern matcher on the given value
     *
     * @param arg value to match against
     * @return a new matcher
     */
    public static <ARG> Matcher<ARG> match(ARG arg)
    {
        return new Matcher<>(arg);
    }

    /**
     * Start a partial matcher on the given value. The partial can be executed at a later
     * time by specifying the value using {@link Partial#with(Object)}
     *
     * @param marker The argument type
     * @return a new partial
     */
    public static <ARG> Partial<ARG> partial(Class<ARG> marker)
    {
        return new PartialImpl<>();
    }

    /**
     * Start a partial matcher on the given value. The partial can be executed at a later
     * time by specifying the value using {@link Partial#with(Object)}
     *
     * @param marker The argument type
     * @return a new partial
     */
    public static <ARG> Partial<ARG> partial(AnyType<ARG> marker)
    {
        return new PartialImpl<>();
    }

    /**
     * Return a new AnyList that matches the given head of a list and the given tail of a list
     *
     * @param head head to match
     * @param tail tail to match
     * @return new AnyList
     */
    public static <T> AnyList headTail(T head, ConsList<T> tail)
    {
        return Any.headTail(head, tail);
    }

    /**
     * Return a new AnyList that matches the given head of a list and any tail of a list
     *
     * @param head head to match
     * @param tail holder for the value of the tail of the list that matches
     * @return new AnyList
     */
    public static <T> AnyList headAnyTail(T head, Any<? extends ConsList<? extends T>> tail)
    {
        return Any.headAnyTail(head, tail);
    }

    /**
     * Return a new AnyList that matches any head of a list and the given tail of a list
     *
     * @param head holder for the value of the head of the list that matches
     * @param tail tail to match
     * @return new AnyList
     */
    public static <T> AnyList anyHeadTail(Any<T> head, ConsList<T> tail)
    {
        return Any.anyHeadTail(head, tail);
    }

    /**
     * Return a new AnyList that matches any head of a list and any tail of a list
     *
     * @param head holder for the value of the head of the list that matches
     * @param tail holder for the value of the tail of the list that matches
     * @return new AnyList
     */
    public static <T> AnyList anyHeadAnyTail(Any<T> head, Any<? extends ConsList<? extends T>> tail)
    {
        return Any.anyHeadAnyTail(head, tail);
    }

    /**
     * Returns an Any that matches any null value
     *
     * @return Any for nulls
     */
    public static AnyNull anyNull()
    {
        return Any.anyNull();
    }

    /**
     * Returns a new Any that matches a present Optional. The given value
     * is assigned the value of the optional on match.
     *
     * @param value will get the value of the optional
     * @return a new any
     */
    public static <T> AnyOptional<T> anySome(Any<T> value)
    {
        return Any.anySome(value);
    }

    /**
     * Returns a new Any that holds an Optional. The given value
     * is assigned the optional itself on match.
     *
     * @param value will get the optional
     * @return a new any
     */
    static <T> AnyOptional<T> anyOptional(Any<Optional<T>> value)
    {
        return Any.anyOptional(value);
    }

    /**
     * Returns a new Any that matches an empty Optional.
     *
     * @return a new any
     */
    public static AnyOptional<Void> anyNone()
    {
        return Any.anyNone();
    }

    <T> Matcher<ARG> register(Tuple fields, Supplier<Boolean> guard, Supplier<T> proc)
    {
        ExtractObject extracter = new ExtractObject(fields, guard, nextBinder);
        addEntry(new Entry<>(extracter, proc));
        nextBinder = null;
        return this;
    }

    @Override
    public <T> Matcher<ARG> caseOf(Tuple fields, Supplier<Boolean> guard, Supplier<T> proc)
    {
        return register(fields, guard, proc);
    }

    @Override
    public <T> Matcher<ARG> caseOf(Tuple fields, Supplier<T> proc)
    {
        return register(fields, null, proc);
    }

    @Override
    public <T> Matcher<ARG> caseOf(Object lhs, Supplier<T> proc)
    {
        return register(Tuple.Tu(lhs), null, proc);
    }

    @Override
    public Matcher<ARG> caseOfUnit(Object lhs, Runnable proc)
    {
        return register(Tuple.Tu(lhs), null, wrap(proc));
    }

    @Override
    public <T> Matcher<ARG> caseOf(Object lhs, Supplier<Boolean> guard, Supplier<T> proc)
    {
        return register(Tuple.Tu(lhs), guard, proc);
    }

    @Override
    public <T> Matcher<ARG> caseOfUnit(Tuple lhs, Supplier<Boolean> guard, Runnable proc)
    {
        return register(lhs, guard, wrap(proc));
    }

    @Override
    public <T> Matcher<ARG> caseOfUnit(Object lhs, Supplier<Boolean> guard, Runnable proc)
    {
        return register(Tuple.Tu(lhs), guard, wrap(proc));
    }

    @Override
    public <T> Matcher<ARG> caseOfUnit(Tuple lhs, Runnable proc)
    {
        return register(lhs, null, wrap(proc));
    }

    @Override
    public <T> Matcher<ARG> caseOfTest(Predicate<ARG> tester, Supplier<T> proc)
    {
        return register(Tuple.Tu(tester), null, proc);
    }

    @Override
    public Matcher<ARG> caseOfTestUnit(Predicate<ARG> tester, Runnable proc)
    {
        return register(Tuple.Tu(tester), null, wrap(proc));
    }

    @Override
    public <T> Matcher<ARG> caseOf(Supplier<T> proc)
    {
        setDefault(proc);
        return this;
    }

    @Override
    public Matcher<ARG> caseOfUnit(Runnable proc)
    {
        setDefault(wrap(proc));
        return this;
    }

    @Override
    public <T> Matcher<ARG> bindTo(Any<T> binder)
    {
        nextBinder = binder;
        return this;
    }

    Matcher(ARG arg)
    {
        super(arg);
    }

    private <T> Supplier<T> wrap(Runnable proc)
    {
        return () -> { proc.run(); return null; };
    }
}

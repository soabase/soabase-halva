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

import io.soabase.halva.any.AnyVal;
import io.soabase.halva.tuple.Tuple;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Main factory for pattern matching
 */
public class MatcherNext<RES, ARG> extends Getter<RES, ARG> implements CasesBase<RES, ARG, MatcherNext<RES, ARG>>
{
    private AnyVal nextBinder = null;

    MatcherNext<RES, ARG> register(Tuple fields, Supplier<Boolean> guard, Supplier<RES> proc)
    {
        ExtractObject extracter = new ExtractObject(fields, guard, nextBinder);
        addEntry(new Entry<>(extracter, proc));
        nextBinder = null;
        return this;
    }

    @Override
    public MatcherNext<RES, ARG> caseOf(Tuple fields, Supplier<Boolean> guard, Supplier<RES> proc)
    {
        return register(fields, guard, proc);
    }

    @Override
    public MatcherNext<RES, ARG> caseOf(Tuple fields, Supplier<RES> proc)
    {
        return register(fields, null, proc);
    }

    @Override
    public MatcherNext<RES, ARG> caseOf(Object lhs, Supplier<RES> proc)
    {
        return register(Tuple.Tu(lhs), null, proc);
    }

    @Override
    public MatcherNext<RES, ARG> caseOfUnit(Object lhs, Runnable proc)
    {
        return register(Tuple.Tu(lhs), null, wrap(proc));
    }

    @Override
    public MatcherNext<RES, ARG> caseOf(Object lhs, Supplier<Boolean> guard, Supplier<RES> proc)
    {
        return register(Tuple.Tu(lhs), guard, proc);
    }

    @Override
    public MatcherNext<RES, ARG> caseOfUnit(Tuple lhs, Supplier<Boolean> guard, Runnable proc)
    {
        return register(lhs, guard, wrap(proc));
    }

    @Override
    public MatcherNext<RES, ARG> caseOfUnit(Object lhs, Supplier<Boolean> guard, Runnable proc)
    {
        return register(Tuple.Tu(lhs), guard, wrap(proc));
    }

    @Override
    public MatcherNext<RES, ARG> caseOfUnit(Tuple lhs, Runnable proc)
    {
        return register(lhs, null, wrap(proc));
    }

    @Override
    public MatcherNext<RES, ARG> caseOfTest(Predicate<ARG> tester, Supplier<RES> proc)
    {
        return register(Tuple.Tu(tester), null, proc);
    }

    @Override
    public MatcherNext<RES, ARG> caseOfTestUnit(Predicate<ARG> tester, Runnable proc)
    {
        return register(Tuple.Tu(tester), null, wrap(proc));
    }

    @Override
    public MatcherNext<RES, ARG> caseOf(Supplier<RES> proc)
    {
        setDefault(proc);
        return this;
    }

    @Override
    public MatcherNext<RES, ARG> caseOfUnit(Runnable proc)
    {
        setDefault(wrap(proc));
        return this;
    }

    @Override
    public MatcherNext<RES, ARG> bindTo(AnyVal<RES> binder)
    {
        nextBinder = binder;
        return this;
    }

    MatcherNext(ARG arg)
    {
        super(arg);
        this.nextBinder = nextBinder;
    }

    private Supplier<RES> wrap(Runnable proc)
    {
        return () -> { proc.run(); return null; };
    }
}

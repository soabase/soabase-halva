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

public class Partial<RES, ARG> implements CasesBase<RES, ARG, Partial<RES, ARG>>, WithBase<RES, ARG>
{
    private final MatcherNext<RES, ARG> matcher;

    Partial()
    {
        matcher = new MatcherNext<>(null);
    }

    @Override
    public Partial<RES, ARG> caseOf(Tuple lhs, Supplier<Boolean> guard, Supplier<RES> proc)
    {
        matcher.caseOf(lhs, guard, proc);
        return this;
    }

    @Override
    public Partial<RES, ARG> caseOf(Tuple lhs, Supplier<RES> proc)
    {
        matcher.caseOf(lhs, proc);
        return this;
    }

    @Override
    public Partial<RES, ARG> caseOf(Object lhs, Supplier<RES> proc)
    {
        matcher.caseOf(lhs, proc);
        return this;
    }

    @Override
    public Partial<RES, ARG> caseOfUnit(Object lhs, Runnable proc)
    {
        matcher.caseOfUnit(lhs, proc);
        return this;
    }

    @Override
    public Partial<RES, ARG> caseOfTest(Predicate<ARG> tester, Supplier<RES> proc)
    {
        matcher.caseOfTest(tester, proc);
        return this;
    }

    @Override
    public Partial<RES, ARG> caseOfTestUnit(Predicate<ARG> tester, Runnable proc)
    {
        matcher.caseOfTestUnit(tester, proc);
        return this;
    }

    @Override
    public Partial<RES, ARG> caseOf(Supplier<RES> proc)
    {
        matcher.caseOf(proc);
        return this;
    }

    @Override
    public Partial<RES, ARG> caseOfUnit(Runnable proc)
    {
        matcher.caseOfUnit(proc);
        return this;
    }

    @Override
    public Partial<RES, ARG> caseOf(Object lhs, Supplier<Boolean> guard, Supplier<RES> proc)
    {
        matcher.caseOf(lhs, guard, proc);
        return this;
    }

    @Override
    public Partial<RES, ARG> caseOfUnit(Tuple lhs, Supplier<Boolean> guard, Runnable proc)
    {
        matcher.caseOfUnit(lhs, guard, proc);
        return this;
    }

    @Override
    public Partial<RES, ARG> caseOfUnit(Object lhs, Supplier<Boolean> guard, Runnable proc)
    {
        matcher.caseOfUnit(lhs, guard, proc);
        return this;
    }

    @Override
    public Partial<RES, ARG> caseOfUnit(Tuple lhs, Runnable proc)
    {
        matcher.caseOfUnit(lhs, proc);
        return this;
    }

    @Override
    public Partial<RES, ARG> bindTo(AnyVal<RES> binder)
    {
        matcher.bindTo(binder);
        return this;
    }

    @Override
    public GettersBase<RES> with(ARG arg)
    {
        return new Getter<>(arg, matcher);
    }
}

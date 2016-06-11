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

import io.soabase.halva.tuple.Tuple;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

class PartialImpl<ARG> implements GettersBase, Partial<ARG>
{
    private final Matcher<ARG> matcher;

    PartialImpl()
    {
        matcher = new Matcher<>(null);
    }

    @Override
    public <T> Partial<ARG> caseOf(Tuple lhs, Guard guard, Supplier<T> proc)
    {
        matcher.caseOf(lhs, guard, proc);
        return this;
    }

    @Override
    public <T> Partial<ARG> caseOf(Tuple lhs, Supplier<T> proc)
    {
        matcher.caseOf(lhs, proc);
        return this;
    }

    @Override
    public <T> Partial<ARG> caseOf(Object lhs, Supplier<T> proc)
    {
        matcher.caseOf(lhs, proc);
        return this;
    }

    @Override
    public Partial<ARG> caseOfUnit(Object lhs, Runnable proc)
    {
        matcher.caseOfUnit(lhs, proc);
        return this;
    }

    @Override
    public <T> Partial<ARG> caseOfTest(Predicate<ARG> tester, Supplier<T> proc)
    {
        matcher.caseOfTest(tester, proc);
        return this;
    }

    @Override
    public Partial<ARG> caseOfTestUnit(Predicate<ARG> tester, Runnable proc)
    {
        matcher.caseOfTestUnit(tester, proc);
        return this;
    }

    @Override
    public <T> Partial<ARG> caseOf(Supplier<T> proc)
    {
        matcher.caseOf(proc);
        return this;
    }

    @Override
    public Partial<ARG> caseOfUnit(Runnable proc)
    {
        matcher.caseOfUnit(proc);
        return this;
    }

    @Override
    public <T> Partial<ARG> caseOf(Object lhs, Guard guard, Supplier<T> proc)
    {
        matcher.caseOf(lhs, guard, proc);
        return this;
    }

    @Override
    public <T> Partial<ARG> caseOfUnit(Tuple lhs, Guard guard, Runnable proc)
    {
        matcher.caseOfUnit(lhs, guard, proc);
        return this;
    }

    @Override
    public <T> Partial<ARG> caseOfUnit(Object lhs, Guard guard, Runnable proc)
    {
        matcher.caseOfUnit(lhs, guard, proc);
        return this;
    }

    @Override
    public <T> Partial<ARG> caseOfUnit(Tuple lhs, Runnable proc)
    {
        matcher.caseOfUnit(lhs, proc);
        return this;
    }

    @Override
    public <T> Optional<T> getOpt()
    {
        return matcher.getOpt();
    }

    @Override
    public <T> T get()
    {
        return matcher.get();
    }

    @Override
    public void apply()
    {
        matcher.apply();
    }

    @Override
    public GettersBase with(ARG arg)
    {
        return new Getter<>(arg, matcher);
    }
}

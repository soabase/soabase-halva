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

import io.soabase.halva.any.AnyType;
import io.soabase.halva.any.AnyVal;
import io.soabase.halva.tuple.Tuple;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Main factory for pattern matching
 */
public class Matcher<ARG>
{
    private final ARG arg;

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
     * @return a new partial
     */
    public static <ARG> PartialFirst<ARG> partial()
    {
        return new PartialFirst<>();
    }

    public <RES> MatcherNext<RES, ARG> as()
    {
        return new MatcherNext<>(arg);
    }

    public <RES> MatcherNext<RES, ARG> as(RES dummy)
    {
        return new MatcherNext<>(arg);
    }

    public <RES> MatcherNext<RES, ARG> as(AnyType<RES> dummy)
    {
        return new MatcherNext<>(arg);
    }

    public <RES> MatcherNext<RES, ARG> as(Class<RES> dummy)
    {
        return new MatcherNext<>(arg);
    }

    public <RES> MatcherNext<RES, ARG> caseOf(Tuple fields, Supplier<Boolean> guard, Supplier<RES> proc)
    {
        return new MatcherNext<RES, ARG>(arg).caseOf(fields, guard, proc);
    }

    public <RES> MatcherNext<RES, ARG> caseOf(Tuple fields, Supplier<RES> proc)
    {
        return new MatcherNext<RES, ARG>(arg).caseOf(fields, proc);
    }

    public <RES> MatcherNext<RES, ARG> caseOf(Object lhs, Supplier<RES> proc)
    {
        return new MatcherNext<RES, ARG>(arg).caseOf(lhs, proc);
    }

    public <RES> MatcherNext<RES, ARG> caseOf(Object lhs, Supplier<Boolean> guard, Supplier<RES> proc)
    {
        return new MatcherNext<RES, ARG>(arg).caseOf(lhs, guard, proc);
    }

    public <RES> MatcherNext<RES, ARG> caseOfTest(Predicate<ARG> tester, Supplier<RES> proc)
    {
        return new MatcherNext<RES, ARG>(arg).caseOfTest(tester, proc);
    }

    public <RES> MatcherNext<RES, ARG> caseOf(Supplier<RES> proc)
    {
        return new MatcherNext<RES, ARG>(arg).caseOf(proc);
    }

    public <RES> MatcherNext<RES, ARG> bindTo(AnyVal<RES> binder)
    {
        return new MatcherNext<RES, ARG>(arg).bindTo(binder);
    }

    Matcher(ARG arg)
    {
        this.arg = arg;
    }
}

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

public class PartialFirst<ARG>
{
    /**
     * Shim to set the result type of the extraction. This is useful if you want to return a super
     * class where expressions generate subclasses.
     *
     * @return this
     */
    public <RES> Partial<RES, ARG> as()
    {
        return new Partial<>();
    }

    /**
     * Shim to set the result type of the extraction. This is useful if you want to return a super
     * class where expressions generate subclasses.
     *
     * @param dummy dummy value to help define the type
     * @return this
     */
    public <RES> Partial<RES, ARG> as(RES dummy)
    {
        return new Partial<>();
    }

    /**
     * Shim to set the result type of the extraction. This is useful if you want to return a super
     * class where expressions generate subclasses.
     *
     * @param dummy dummy value to help define the type
     * @return this
     */
    public <RES> Partial<RES, ARG> as(AnyType<RES> dummy)
    {
        return new Partial<>();
    }

    /**
     * Shim to set the result type of the extraction. This is useful if you want to return a super
     * class where expressions generate subclasses.
     *
     * @param dummy dummy value to help define the type
     * @return this
     */
    public <RES> Partial<RES, ARG> as(Class<RES> dummy)
    {
        return new Partial<>();
    }

    /**
     * same as {@link CasesBase#caseOf(Tuple, Supplier, Supplier)}
     */
    public <RES> Partial<RES, ARG> caseOf(Tuple fields, Supplier<Boolean> guard, Supplier<RES> proc)
    {
        return new Partial<RES, ARG>().caseOf(fields, guard, proc);
    }

    /**
     * same as {@link CasesBase#caseOf(Tuple, Supplier)}
     */
    public <RES> Partial<RES, ARG> caseOf(Tuple fields, Supplier<RES> proc)
    {
        return new Partial<RES, ARG>().caseOf(fields, proc);
    }

    /**
     * same as {@link CasesBase#caseOf(Object, Supplier)}
     */
    public <RES> Partial<RES, ARG> caseOf(Object lhs, Supplier<RES> proc)
    {
        return new Partial<RES, ARG>().caseOf(lhs, proc);
    }

    /**
     * same as {@link CasesBase#caseOf(Object, Supplier, Supplier)}
     */
    public <RES> Partial<RES, ARG> caseOf(Object lhs, Supplier<Boolean> guard, Supplier<RES> proc)
    {
        return new Partial<RES, ARG>().caseOf(lhs, guard, proc);
    }

    /**
     * same as {@link CasesBase#caseOfTest(Predicate, Supplier)}
     */
    public <RES> Partial<RES, ARG> caseOfTest(Predicate<ARG> tester, Supplier<RES> proc)
    {
        return new Partial<RES, ARG>().caseOfTest(tester, proc);
    }

    /**
     * same as {@link CasesBase#caseOf(Supplier)}
     */
    public <RES> Partial<RES, ARG> caseOf(Supplier<RES> proc)
    {
        return new Partial<RES, ARG>().caseOf(proc);
    }

    /**
     * same as {@link CasesBase#bindTo(AnyVal)}
     */
    public <RES> Partial<RES, ARG> bindTo(AnyVal<RES> binder)
    {
        return new Partial<RES, ARG>().bindTo(binder);
    }

    PartialFirst()
    {
    }
}

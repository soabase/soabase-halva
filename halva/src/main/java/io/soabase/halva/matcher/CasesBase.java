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
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface CasesBase<ARG, M>
{
    <T> M caseOf(Tuple lhs, Guard guard, Supplier<T> proc);

    <T> M caseOf(Object lhs, Guard guard, Supplier<T> proc);

    <T> M caseOf(Tuple lhs, Supplier<T> proc);

    <T> M caseOf(Object lhs, Supplier<T> proc);

    <T> M caseOfUnit(Tuple lhs, Guard guard, Runnable proc);

    <T> M caseOfUnit(Object lhs, Guard guard, Runnable proc);

    <T> M caseOfUnit(Tuple lhs, Runnable proc);

    <T> M caseOfUnit(Object lhs, Runnable proc);

    <T> M caseOfTest(Predicate<ARG> tester, Supplier<T> proc);

    M caseOfTestUnit(Predicate<ARG> tester, Runnable proc);

    <T> M caseOf(Supplier<T> proc);

    M caseOfUnit(Runnable proc);
}

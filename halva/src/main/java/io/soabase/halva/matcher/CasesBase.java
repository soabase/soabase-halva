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

public interface CasesBase<RES, ARG, M>
{
    /**
     * Add a case to the pattern matcher with a guard test. If the value matches
     * and the guard passes, the proc is executed.
     *
     * @param lhs possible match
     * @param guard test
     * @param proc proc to execute on match
     * @return this
     */
    M caseOf(Tuple lhs, Supplier<Boolean> guard, Supplier<RES> proc);

    /**
     * Add a case to the pattern matcher with a guard test. If the value matches
     * and the guard passes, the proc is executed.
     *
     * @param lhs possible match
     * @param guard test
     * @param proc proc to execute on match
     * @return this
     */
    M caseOf(Object lhs, Supplier<Boolean> guard, Supplier<RES> proc);

    /**
     * Add a case to the pattern matcher. If the value matches,
     * the proc is executed.
     *
     * @param lhs possible match
     * @param proc proc to execute on match
     * @return this
     */
    M caseOf(Tuple lhs, Supplier<RES> proc);

    /**
     * Add a case to the pattern matcher. If the value matches,
     * the proc is executed.
     *
     * @param lhs possible match
     * @param proc proc to execute on match
     * @return this
     */
    M caseOf(Object lhs, Supplier<RES> proc);

    /**
     * Add a case to the pattern matcher with a guard test. If the value matches
     * and the guard passes, the proc is executed.
     *
     * @param lhs possible match
     * @param guard test
     * @param proc proc to execute on match
     * @return this
     */
    M caseOfUnit(Tuple lhs, Supplier<Boolean> guard, Runnable proc);

    /**
     * Add a case to the pattern matcher with a guard test. If the value matches
     * and the guard passes, the proc is executed.
     *
     * @param lhs possible match
     * @param guard test
     * @param proc proc to execute on match
     * @return this
     */
    M caseOfUnit(Object lhs, Supplier<Boolean> guard, Runnable proc);

    /**
     * Add a case to the pattern matcher. If the value matches,
     * the proc is executed.
     *
     * @param lhs possible match
     * @param proc proc to execute on match
     * @return this
     */
    M caseOfUnit(Tuple lhs, Runnable proc);

    /**
     * Add a case to the pattern matcher. If the value matches,
     * the proc is executed.
     *
     * @param lhs possible match
     * @param proc proc to execute on match
     * @return this
     */
    M caseOfUnit(Object lhs, Runnable proc);

    /**
     * The predicate is executed. If the test passes, the proc is executed
     *
     * @param tester tester
     * @param proc proc
     * @return this
     */
    M caseOfTest(Predicate<ARG> tester, Supplier<RES> proc);

    /**
     * The predicate is executed. If the test passes, the proc is executed
     *
     * @param tester tester
     * @param proc proc
     * @return this
     */
    M caseOfTestUnit(Predicate<ARG> tester, Runnable proc);

    /**
     * The default case - if there are no matches in any other cases,
     * the proc is run
     *
     * @param proc the proc
     * @return this
     */
    M caseOf(Supplier<RES> proc);

    /**
     * The default case - if there are no matches in any other cases,
     * the proc is run
     *
     * @param proc the proc
     * @return this
     */
    M caseOfUnit(Runnable proc);

    /**
     * Emulates Scala's "@" binding argument. If the case matches, <code>binder</code>
     * will get set to the argument passed to the matcher/partial
     *
     * @param binder value to hold the match
     * @return this
     */
    M bindTo(AnyVal<RES> binder);
}

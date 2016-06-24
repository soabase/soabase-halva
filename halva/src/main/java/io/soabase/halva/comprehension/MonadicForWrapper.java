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
package io.soabase.halva.comprehension;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Defines how a given (monadic) type should implement map/flatMap and optionally filter
 * The specification isn't type safe so you'll have to be careful for these 2 lines...
 * (which has meaning in some cases, eg Optional/Stream and not in others, eg CompletableFuture, Validation)
 *
 * @param <MM> - the monad class, ie the same as the <code>M</code> in the parent class
 */
public interface MonadicForWrapper<MM>
{
    <A> MM flatMap(MM m, Function<A, ? extends MM> flatMapper);

    <A, B> MM map(MM m, Function<A, B> mapper);

    default MM filter(MM m, Predicate predicate)
    {
        throw new UnsupportedOperationException("filtering not supported");
    }
}

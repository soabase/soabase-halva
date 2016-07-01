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

import java.util.Optional;
import java.util.function.Function;

@MonadicFor
public class ReaderOptionalForFactory<A>  implements MonadicForWrapper<Reader<A, Optional>> {

    @SuppressWarnings("unchecked")
    public <B> Reader<A, Optional/*C*/> flatMap(final Reader<A, Optional/*B*/> m, final Function<B, ? extends Reader<A, Optional/*C*/>> flat_mapper) {
        // Java type inference needs a _lot_ of help here
        // But the basic idea is very simple - the flatMap "m" takes an optional as input ... if it's present then run the function as before, else return the constant "empty"
        return null;
    }

    @SuppressWarnings("unchecked")
    public <B, C> Reader<A, Optional/*C*/> map(final Reader<A, Optional/*B*/> m, final Function<B, C> mapper) {
        // Same as the above basically
        return null;
    }
}
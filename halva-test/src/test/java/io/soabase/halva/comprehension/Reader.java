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

public class Reader<A, B>
{
    public final <C> Reader<A, C> map(Function<B, C> f)
    {
        return null;
    }

    public final <C> Reader<A, C> flatMap(Function<B, Reader<A, C>> f)
    {
        return null;
    }
}

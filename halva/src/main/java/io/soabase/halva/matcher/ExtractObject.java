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
import java.util.function.Supplier;

class ExtractObject
{
    private final Tuple values;
    private final Supplier<Boolean> guard;

    ExtractObject(Tuple values, Supplier<Boolean> guard)
    {
        this.values = values;
        this.guard = guard;
    }

    boolean extract(Object from)
    {
        return values.extract(from) && ((guard == null) || guard.get());
    }
}

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
package io.soabase.halva.any;

class AnyNull extends AnyVal<Object>
{
    static final AnyNull instance = new AnyNull();

    AnyNull()
    {
        super(false, false, false);
    }

    @Override
    public Object val()
    {
        return null;
    }

    @Override
    public void set(Object value)
    {
        // NOP
    }

    @Override
    public boolean canSet(Object value)
    {
        return value == Void.TYPE;
    }
}

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
package io.soabase.halva.processor;

import com.squareup.javapoet.ClassName;

public class GeneratedClass
{
    private final ClassName original;
    private final ClassName generated;

    public GeneratedClass(ClassName original, ClassName generated)
    {
        this.original = original;
        this.generated = generated;
    }

    public ClassName get()
    {
        return (generated != null) ? generated : original;
    }

    public boolean hasGenerated()
    {
        return generated != null;
    }

    public ClassName getOriginal()
    {
        return original;
    }

    public ClassName getGenerated()
    {
        return generated;
    }
}

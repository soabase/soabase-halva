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
package io.soabase.halva.processor.implicit;

import io.soabase.halva.processor.SpecBase;
import javax.lang.model.element.TypeElement;

class ImplicitPairSpec implements SpecBase
{
    private final ImplicitSpec implicitSpec;
    private final ContextSpec contextSpec;

    @Override
    public TypeElement getAnnotatedElement()
    {
        return (implicitSpec != null) ? implicitSpec.getAnnotatedElement() : contextSpec.getAnnotatedElement();
    }

    ImplicitPairSpec(ImplicitSpec implicitSpec)
    {
        this.implicitSpec = implicitSpec;
        contextSpec = null;
    }

    ImplicitPairSpec(ContextSpec contextSpec)
    {
        implicitSpec = null;
        this.contextSpec = contextSpec;
    }

    ImplicitSpec getImplicitSpec()
    {
        return implicitSpec;
    }

    ContextSpec getContextSpec()
    {
        return contextSpec;
    }
}

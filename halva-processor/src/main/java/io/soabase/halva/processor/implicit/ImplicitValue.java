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

import com.squareup.javapoet.CodeBlock;
import io.soabase.halva.processor.Environment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

class ImplicitValue
{
    private final Environment environment;
    private final List<ContextItem> contextItems;
    private final FoundImplicit foundImplicit;

    ImplicitValue(Environment environment, List<ContextItem> contextItems, FoundImplicit foundImplicit)
    {
        this.environment = environment;
        this.contextItems = contextItems;
        this.foundImplicit = foundImplicit;
    }

    CodeBlock build()
    {
        CodeBlock.Builder builder = CodeBlock.builder();
        if ( foundImplicit != null )
        {
            addDirectValue(builder);
        }
        else
        {
            builder.add("null");
        }
        return builder.build();
    }

    private void addDirectValue(CodeBlock.Builder builder)
    {
        Element element = foundImplicit.getElement();
        if ( element.getKind() == ElementKind.FIELD )
        {
            builder.add("$T.$L", element.getEnclosingElement().asType(), element.getSimpleName());
        }
        else
        {
            ExecutableElement method = (ExecutableElement)element;
            AtomicBoolean isFirst = new AtomicBoolean(false);
            CodeBlock methodCode = new ImplicitMethod(environment, method, contextItems).build();
            builder.add("$T.$L(", element.getEnclosingElement().asType(), element.getSimpleName());
            builder.add(methodCode);
            builder.add(")");
        }
    }
}

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
import io.soabase.halva.implicit.Implicit;
import io.soabase.halva.processor.Environment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

class ImplicitMethod
{
    private final Environment environment;
    private final GenericMapContext genericMapContext;
    private final ExecutableElement method;
    private final List<ContextItem> contextItems;

    ImplicitMethod(Environment environment, GenericMapContext genericMapContext, ExecutableElement method, List<ContextItem> contextItems)
    {
        this.environment = environment;
        this.genericMapContext = genericMapContext;
        this.method = method;
        this.contextItems = contextItems;
    }

    CodeBlock build()
    {
        return build(p -> {});
    }

    CodeBlock build(Consumer<VariableElement> parameterAdded)
    {
        CodeBlock.Builder builder = CodeBlock.builder();
        AtomicBoolean isFirst = new AtomicBoolean(false);
        method.getParameters().forEach(parameter -> {
            if ( !isFirst.compareAndSet(false, true) )
            {
                builder.add(", ");
            }
            if ( parameter.getAnnotation(Implicit.class) != null )
            {
                FoundImplicit foundImplicit = new ImplicitSearcher(environment, genericMapContext, contextItems).find(parameter.asType());
                CodeBlock value = new ImplicitValue(environment, genericMapContext, contextItems, foundImplicit).build();
                builder.add(value);
            }
            else
            {
                parameterAdded.accept(parameter);
                builder.add(parameter.getSimpleName().toString());
            }
        });
        return builder.build();
    }
}

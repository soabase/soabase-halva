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

    CodeBlock build(Element parent)
    {
        CodeBlock.Builder builder = CodeBlock.builder();
        if ( foundImplicit != null )
        {
            if ( foundImplicit.isMultiItem() )
            {
                environment.error(parent, "Specific generic implementation needed as parameter for " + parent);
                builder.add("null");
            }
            else
            {
                addDirectValue(builder);
            }
        }
        else
        {
            builder.add("null");
        }
        return builder.build();
    }

    private void addDirectValue(CodeBlock.Builder builder)
    {
        Element element =  foundImplicit.getItems().get(0).getElement();
        if ( element.getKind() == ElementKind.FIELD )
        {
            builder.add("$T.$L", element.getEnclosingElement().asType(), element.getSimpleName());
        }
        else
        {
            ExecutableElement method = (ExecutableElement)element;
            ImplicitMethod implicitMethod = new ImplicitMethod(environment, method, contextItems);
            if ( implicitMethod.hasMultiGenericMethods() )
            {
                environment.error(element, "Specific generic implementation needed as parameter for " + element);
                builder.add("null");
            }
            else
            {
                CodeBlock methodCode = implicitMethod.build();
                builder.add("$T.$L(", element.getEnclosingElement().asType(), element.getSimpleName());
                builder.add(methodCode);
                builder.add(")");
            }
        }
    }
}

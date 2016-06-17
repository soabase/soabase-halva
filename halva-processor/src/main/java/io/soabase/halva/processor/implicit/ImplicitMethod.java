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
import io.soabase.halva.sugar.ConsList;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static io.soabase.halva.sugar.Sugar.*;

class ImplicitMethod
{
    private final Environment environment;
    private final ExecutableElement method;
    private final List<ContextItem> contextItems;
    private final ConsList<ImplicitParameter> implicitParameters;
    private final String methodName;

    ImplicitMethod(Environment environment, ExecutableElement method, List<ContextItem> contextItems)
    {
        this.environment = environment;
        this.method = method;
        this.contextItems = contextItems;
        methodName = method.getSimpleName().toString();

        implicitParameters = ConsList(method.getParameters().stream().map(parameter -> {
            if ( parameter.getAnnotation(Implicit.class) != null )
            {
                FoundImplicit foundImplicit = new ImplicitSearcher(environment, contextItems).find(parameter.asType());
                return new ImplicitParameter(parameter, foundImplicit);
            }
            return new ImplicitParameter(parameter, new FoundImplicit());
        }).collect(Collectors.toList()));
    }

    private ImplicitMethod(Environment environment, ExecutableElement method, String methodName, List<ContextItem> contextItems, ConsList<ImplicitParameter> implicitParameters)
    {
        this.environment = environment;
        this.method = method;
        this.methodName = methodName;
        this.contextItems = contextItems;
        this.implicitParameters = implicitParameters;
    }

    String getMethodName()
    {
        return methodName;
    }

    boolean hasMultiGenericMethods()
    {
        return implicitParameters.stream().anyMatch(parameter -> parameter.getFoundImplicit().isMultiItem());
    }

    private static void buildMultiGenericMethodsHelper(ConsList<ImplicitParameter> work, ConsList<ImplicitParameter> list, ArrayList<List<ImplicitParameter>> result)
    {
        if ( list.isEmpty() )
        {
            result.add(work);
        }
        else
        {
            for ( ImplicitParameter parameter : list )
            {
                if ( parameter.getFoundImplicit().hasImplicits() )
                {
                    for ( ContextItem item : parameter.getFoundImplicit().getItems() )
                    {
                        ImplicitParameter head = new ImplicitParameter(parameter.getParameter(), new FoundImplicit(item));
                        buildMultiGenericMethodsHelper(concat(head, work), list.tail(), result);
                    }
                }
                else
                {
                    buildMultiGenericMethodsHelper(concat(parameter, work), list.tail(), result);
                }
            }
        }
    }

    List<ImplicitMethod> buildMultiGenericMethods()
    {
        ArrayList<List<ImplicitParameter>> result = new ArrayList<>();
        buildMultiGenericMethodsHelper(List(), implicitParameters, result);
        AtomicInteger index = new AtomicInteger();
        return result.stream()
            .map(list -> new ImplicitMethod(environment, method, methodName + getSuffix(index.getAndIncrement()), contextItems, ConsList(list)))
            .collect(Collectors.toList());
    }

    List<VariableElement> getNonImplicitParameters()
    {
        return implicitParameters.stream().filter(parameter -> !parameter.getFoundImplicit().isMultiItem()).map(ImplicitParameter::getParameter).collect(Collectors.toList());
    }

    CodeBlock build()
    {
        CodeBlock.Builder builder = CodeBlock.builder();
        AtomicBoolean isFirst = new AtomicBoolean(false);
        implicitParameters.forEach(parameter -> {
            if ( !isFirst.compareAndSet(false, true) )
            {
                builder.add(", ");
            }
            if ( parameter.getFoundImplicit().hasImplicits() )
            {
                CodeBlock value = new ImplicitValue(environment, contextItems, parameter.getFoundImplicit()).build(method);
                builder.add(value);
            }
            else
            {
                builder.add(parameter.getParameter().getSimpleName().toString());
            }
        });
        return builder.build();
    }

    private static String getSuffix(int index)
    {
        String[] vals = {"A", "B", "C", "D", "E", "F"};
        String val = vals[index % vals.length];
        return (index >= vals.length) ? (val + getSuffix(index / vals.length)) : val;
    }
}

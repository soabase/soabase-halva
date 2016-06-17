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

import io.soabase.halva.implicit.Implicit;
import io.soabase.halva.processor.Environment;
import io.soabase.halva.processor.Pass;
import io.soabase.halva.processor.WorkItem;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class PassProcessImplicits implements Pass
{
    private final Environment environment;
    private final List<WorkItem> implicitClassItems;
    private final List<ContextItem> contextItems;

    PassProcessImplicits(Environment environment, List<WorkItem> implicitClassItems, List<ContextItem> contextItems)
    {
        this.environment = environment;
        this.implicitClassItems = implicitClassItems;
        this.contextItems = contextItems;
    }

    @Override
    public Optional<Pass> process()
    {
        List<ImplicitSpec> specs = new ArrayList<>();
        implicitClassItems.forEach(item -> {
            Element element = item.getElement();
            do
            {
                if ( element.getKind() != ElementKind.CLASS )
                {
                    environment.error(element, "@ImplicitClass can only be applied to classes");
                    break;
                }

                TypeElement typeElement = (TypeElement)element;
                if ( typeElement.getModifiers().contains(Modifier.FINAL) || typeElement.getModifiers().contains(Modifier.PRIVATE) )
                {
                    environment.error(element, "@ImplicitClass cannot be applied to final or private classes");
                    break;
                }

                List<ImplicitItem> items = new ArrayList<>();
                item.getElement().getEnclosedElements().forEach(child -> {
                    if ( (child.getKind() == ElementKind.METHOD) || (child.getKind() == ElementKind.CONSTRUCTOR) )
                    {
                        ExecutableElement executable = (ExecutableElement)child;
                        boolean hasImplicits = executable.getParameters().stream().anyMatch(parameter -> parameter.getAnnotation(Implicit.class) != null);
                        boolean isPrivateOrFinal = executable.getModifiers().contains(Modifier.PRIVATE) || executable.getModifiers().contains(Modifier.FINAL);
                        if ( hasImplicits )
                        {
                            if ( executable.getModifiers().contains(Modifier.PRIVATE) )
                            {
                                environment.error(executable, "Implicits cannot be applied to private methods");
                            }
                            else if ( executable.getModifiers().contains(Modifier.FINAL) )
                            {
                                environment.error(executable, "Implicits cannot be applied to final methods");
                            }
                            else
                            {
                                items.add(new ImplicitItem(executable));
                            }
                        }
                        else if ( (child.getKind() == ElementKind.CONSTRUCTOR) && !isPrivateOrFinal )
                        {
                            items.add(new ImplicitItem(executable));
                        }
                    }
                });

                specs.add(new ImplicitSpec(typeElement, item.getAnnotationReader(), items));
            } while ( false );
        });
        return Optional.of(new PassCreate(environment, specs, contextItems));
    }
}

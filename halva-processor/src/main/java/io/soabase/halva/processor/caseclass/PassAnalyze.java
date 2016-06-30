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
package io.soabase.halva.processor.caseclass;

import io.soabase.halva.caseclass.CaseClassIgnore;
import io.soabase.halva.caseclass.CaseClassMutable;
import io.soabase.halva.caseclass.CaseObject;
import io.soabase.halva.processor.Environment;
import io.soabase.halva.processor.Pass;
import io.soabase.halva.processor.WorkItem;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class PassAnalyze implements Pass
{
    private final Environment environment;
    private final List<WorkItem> workItems;

    PassAnalyze(Environment environment, List<WorkItem> workItems)
    {
        this.environment = environment;
        this.workItems = workItems;
    }

    @Override
    public Optional<Pass> process()
    {
        List<CaseClassSpec> specs = new ArrayList<>();
        workItems.forEach(item -> {
            Element element = item.getElement();
            do
            {
                if ( element.getKind() != ElementKind.INTERFACE )
                {
                    environment.error(element, "@CaseClass can only be applied to interfaces");
                    break;
                }

                List<CaseClassItem> caseClassItems = new ArrayList<>();
                TypeElement typeElement = (TypeElement)element;
                typeElement.getEnclosedElements().forEach(child -> {
                    do
                    {
                        if ( child.getKind() != ElementKind.METHOD )
                        {
                            break;
                        }

                        ExecutableElement executable = (ExecutableElement)child;
                        if ( (executable.getParameters().size() > 0) || (executable.getAnnotation(CaseClassIgnore.class) != null) )
                        {
                            if ( executable.isDefault() )
                            {
                                break;
                            }
                            environment.error(element, "Non-CaseClass/CaseObject methods must have a default implementation");
                            break;
                        }

                        boolean mutable = (executable.getAnnotation(CaseClassMutable.class) != null);
                        if ( item.getAnnotationReader().getName().equals(CaseObject.class.getSimpleName()) )
                        {
                            if ( mutable )
                            {
                                environment.error(element, "@CaseClassMutable cannot be used with @CaseObject");
                                break;
                            }
                            break;
                        }
                        else
                        {
                            if ( executable.getReturnType().getKind() == TypeKind.VOID )
                            {
                                environment.error(element, "@CaseClass/CaseObject methods cannot return void");
                                break;
                            }
                        }

                        CaseClassItem caseClassItem = new CaseClassItem(executable.getSimpleName().toString(), executable, executable.getReturnType(), executable.isDefault(), mutable);
                        caseClassItems.add(caseClassItem);
                    } while ( false );
                });

                environment.getGeneratedManager().registerGenerated(typeElement, item.getAnnotationReader());
                specs.add(new CaseClassSpec(typeElement, item.getAnnotationReader(), caseClassItems));
            } while ( false );
        });
        return Optional.of(new PassCreateClass(environment, specs));
    }
}

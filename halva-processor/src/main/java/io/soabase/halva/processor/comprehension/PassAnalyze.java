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
package io.soabase.halva.processor.comprehension;

import io.soabase.halva.processor.Environment;
import io.soabase.halva.processor.Pass;
import io.soabase.halva.processor.WorkItem;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
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
        List<MonadicSpec> monadicSpecs = new ArrayList<>();
        TypeMirror wrapperType = environment.getTypeUtils().erasure(environment.getElementUtils().getTypeElement("io.soabase.halva.comprehension.MonadicForWrapper").asType());
        workItems.forEach(item -> {
            TypeElement element = (TypeElement)item.getElement();   // by definition - annotation is only for Type
            if ( element.getKind() == ElementKind.CLASS )
            {
                Optional<TypeElement> foundMonadElement = element.getInterfaces().stream().map(type -> {
                    TypeMirror erasure = environment.getTypeUtils().erasure(type);
                    do
                    {
                        if ( !environment.getTypeUtils().isSameType(wrapperType, erasure) )
                        {
                             break;
                        }

                        DeclaredType declaredType = (DeclaredType)type;
                        if ( declaredType.getTypeArguments().size() != 1 )
                        {
                            environment.error(item.getElement(), "MonadicForWrapper must be parameterized: " + item.getElement());
                            break;
                        }

                        TypeElement monadElement = environment.getElementUtils().getTypeElement((declaredType.getTypeArguments().get(0)).toString());
                        if ( monadElement.getTypeParameters().size() == 0 )
                        {
                            environment.error(item.getElement(), "MonadicForWrapper argument is not monadic. It must take type parameters: " + monadElement);
                            break;
                        }

                        return Optional.of(monadElement);
                    } while ( false );

                    return Optional.<TypeElement>empty();
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

                if ( foundMonadElement.isPresent() )
                {
                    monadicSpecs.add(new MonadicSpec(element, foundMonadElement.get(), item.getAnnotationReader()));
                }
            }
            else
            {
                environment.error(item.getElement(), "@MonadicFor cannot be applied to interfaces");
            }
        });
        return Optional.of(new PassCreate(environment, monadicSpecs));
    }
}

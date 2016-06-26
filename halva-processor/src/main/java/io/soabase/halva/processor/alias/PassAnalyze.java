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
package io.soabase.halva.processor.alias;

import io.soabase.halva.processor.Environment;
import io.soabase.halva.processor.Pass;
import io.soabase.halva.processor.WorkItem;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
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
        List<AliasSpec> specs = new ArrayList<>();
        workItems.forEach(item -> {
            AliasSpec spec = null;
            Element element = item.getElement();
            if ( element.getKind() == ElementKind.INTERFACE )
            {
                spec = isApplicableInterface(item, element);
            }
            else if ( element.getKind() == ElementKind.CLASS )
            {
                spec = isApplicableClass(item, element);
            }
            else
            {
                environment.error(element, "@TypeAlias can only be applied to classes or interfaces");
            }
            if ( spec != null )
            {
                specs.add(spec);
            }
        });
        return Optional.of(new PassCreate(environment, specs));
    }

    private AliasSpec isApplicableClass(WorkItem item, Element element)
    {
        TypeElement typeElement = (TypeElement)element;

        if ( typeElement.getEnclosedElements().size() == 1 )    // 1 for the default constructor
        {
            Element child = typeElement.getEnclosedElements().get(0);
            if ( (child.getKind() != ElementKind.CONSTRUCTOR) || (((ExecutableElement)child).getParameters().size() > 0) )
            {
                environment.error(element, "@TypeAlias must be completely empty");
                return null;
            }
        }
        else
        {
            environment.error(element, "@TypeAlias must be completely empty");
            return null;
        }

        TypeMirror parentType = typeElement.getSuperclass();
        if ( parentType.getKind() == TypeKind.NONE )
        {
            environment.error(element, "@TypeAlias classes must extend a type that is to be aliased");
        }

        boolean hasNoArgConstructor = false;
        for ( Element child : environment.getTypeUtils().asElement(parentType).getEnclosedElements() )
        {
            if ( child.getKind() == ElementKind.CONSTRUCTOR )
            {
                if ( child.getModifiers().contains(Modifier.PUBLIC) || child.getModifiers().contains(Modifier.PROTECTED) )
                {
                    if ( ((ExecutableElement)child).getParameters().size() == 0 )
                    {
                        hasNoArgConstructor = true;
                    }
                }
            }
            else if ( child.getKind() == ElementKind.METHOD )
            {
                if ( child.getModifiers().contains(Modifier.PUBLIC) && child.getModifiers().contains(Modifier.FINAL) )
                {
                    environment.error(element, "Aliased class cannot contain public final methods");
                    return null;
                }
            }
        }

        if ( !hasNoArgConstructor )
        {
            environment.error(element, "Aliased class must contain a public or protected, non-final, no-arg constructor");
            return null;
        }

        return new AliasSpec(typeElement, item.getAnnotationReader(), (DeclaredType)parentType);
    }

    private AliasSpec isApplicableInterface(WorkItem item, Element element)
    {
        TypeElement typeElement = (TypeElement)element;
        if ( typeElement.getInterfaces().size() != 1 )
        {
            environment.error(element, "@TypeAlias interfaces must extend a type that is to be aliased");
            return null;
        }
        TypeMirror parentType = typeElement.getInterfaces().get(0);

        if ( typeElement.getEnclosedElements().size() != 0 )
        {
            environment.error(element, "@TypeAlias must be completely empty");
            return null;
        }

        return new AliasSpec(typeElement, item.getAnnotationReader(), (DeclaredType)parentType);
    }
}

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

import io.soabase.halva.processor.Environment;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

class ImplicitSearcher
{
    private final Environment environment;
    private final ImplicitSpec implicitClassSpec;
    private final List<ContextItem> contextItems;

    ImplicitSearcher(Environment environment, ImplicitSpec implicitClassSpec, List<ContextItem> contextItems)
    {
        this.environment = environment;
        this.implicitClassSpec = implicitClassSpec;
        this.contextItems = contextItems;
    }

    FoundImplicit find(TypeMirror type)
    {
        List<FoundImplicit> foundImplicits = new ArrayList<>();
        contextItems.stream().filter(item -> {
            List<TypeMirror> limitContexts = implicitClassSpec.getAnnotationReader().getClasses("limitContexts");
            List<TypeMirror> excludeContexts = implicitClassSpec.getAnnotationReader().getClasses("excludeContexts");
            List<TypeMirror> limits = item.getAnnotationReader().getClasses("limits");
            List<TypeMirror> excludes = item.getAnnotationReader().getClasses("excludes");

            if ( !contains(limitContexts, item.getClassElement().asType(), true) )
            {
                return false;
            }
            if ( contains(excludeContexts, item.getClassElement().asType(), false) )
            {
                return false;
            }
            if ( !contains(limits, implicitClassSpec.getAnnotatedElement().asType(), true) )
            {
                return false;
            }
            //noinspection RedundantIfStatement
            if ( contains(excludes, implicitClassSpec.getAnnotatedElement().asType(), false) )
            {
                return false;
            }

            return true;
        }).forEach(item -> {
            Element element = item.getElement();
            TypeMirror compareType = environment.typeOfFieldOrMethod(element);
            if ( environment.getTypeUtils().isAssignable(compareType, type) )
            {
                foundImplicits.add(new FoundImplicit(element));
            }
        });

        if ( foundImplicits.size() != 1 )
        {
            String message = (foundImplicits.size() == 0) ? "No matches found for implicit for " : "Multiple matches found for implicit for ";
            environment.error(environment.getTypeUtils().asElement(type), message + type);
            return null;
        }

        return foundImplicits.get(0);
    }

    private boolean contains(List<TypeMirror> checks, TypeMirror checkType, boolean defaultResult)
    {
        TypeMirror erasedType = environment.getTypeUtils().erasure(checkType);
        if ( checks.size() > 0 )
        {
            return checks.stream().anyMatch(type -> environment.getTypeUtils().isSameType(type, erasedType));
        }
        return defaultResult;
    }
}

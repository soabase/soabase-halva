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
package io.soabase.halva.processor.container;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import io.soabase.halva.processor.AnnotationReader;
import io.soabase.halva.processor.Environment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Container
{
    private final Environment environment;
    private final TypeElement element;
    private final AnnotationReader annotationReader;
    private final List<TypeSpec> addedItems = new ArrayList<>();

    public Container(Environment environment, TypeElement element, AnnotationReader annotationReader)
    {
        this.environment = environment;
        this.element = element;
        this.annotationReader = annotationReader;
    }

    public TypeElement getElement()
    {
        return element;
    }

    public AnnotationReader getAnnotationReader()
    {
        return annotationReader;
    }

    public void addItem(TypeSpec.Builder builder)
    {
        builder.addModifiers(Modifier.STATIC);
        addedItems.add(builder.build());
    }

    public TypeSpec.Builder build(ClassName qualifiedClassName)
    {
        Collection<Modifier> modifiers = environment.getModifiers(element);
        TypeSpec.Builder builder = TypeSpec.classBuilder(qualifiedClassName)
            .addModifiers(modifiers.toArray(new Modifier[modifiers.size()]));
        addedItems.forEach(builder::addType);
        return builder;
    }
}

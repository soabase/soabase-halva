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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import io.soabase.halva.implicit.Implicit;
import io.soabase.halva.implicit.ImplicitClass;
import io.soabase.halva.processor.ProcessorBase;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("io.soabase.halva.implicit.ImplicitClass")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ImplicitClassProcessor extends ProcessorBase<ImplicitSpec, Templates>
{
    private static final ImplicitItem error = new ImplicitItem();
    private static final ImplicitItem ignore = new ImplicitItem();

    public ImplicitClassProcessor()
    {
        super(ImplicitClass.class);
    }

    @Override
    protected ImplicitSpec getItems(Element element)
    {
        //noinspection LoopStatementThatDoesntLoop
        do
        {
            if ( element.getKind() != ElementKind.CLASS )
            {
                error(element, "@ImplicitClass can only be applied to classes");
                break;
            }

            TypeElement typeElement = (TypeElement)element;
            if ( typeElement.getModifiers().contains(Modifier.FINAL) || typeElement.getModifiers().contains(Modifier.PRIVATE) )
            {
                error(element, "@ImplicitClass cannot be applied to final or private classes");
                break;
            }

            List<ImplicitItem> items = typeElement.getEnclosedElements().stream().map(child -> {
                if ( (child.getKind() == ElementKind.METHOD) || (child.getKind() == ElementKind.CONSTRUCTOR) )
                {
                    ExecutableElement executable = (ExecutableElement)child;
                    boolean hasImplicits = executable.getParameters().stream().anyMatch(parameter -> parameter.getAnnotation(Implicit.class) != null);
                    boolean isPrivateOrFinal = executable.getModifiers().contains(Modifier.PRIVATE) || executable.getModifiers().contains(Modifier.FINAL);
                    if ( hasImplicits )
                    {
                        if ( executable.getModifiers().contains(Modifier.PRIVATE) )
                        {
                            error(executable, "Implicits cannot be applied to private methods");
                            return error;
                        }

                        if ( executable.getModifiers().contains(Modifier.FINAL) )
                        {
                            error(executable, "Implicits cannot be applied to final methods");
                            return error;
                        }
                        return new ImplicitItem(executable);
                    }

                    if ( (child.getKind() == ElementKind.CONSTRUCTOR) && !isPrivateOrFinal )
                    {
                        return new ImplicitItem(executable);
                    }
                }
                return ignore;
            })
            .filter(item -> item != ignore)
            .collect(Collectors.toList());
            if ( items.contains(error) )
            {
                break;
            }

            return new ImplicitSpec(typeElement, items);
        } while ( false );

        return new ImplicitSpec();
    }

    @Override
    protected Templates newTemplates()
    {
        return new Templates();
    }

    @Override
    protected void buildClass(Templates templates, ImplicitSpec spec)
    {
        if ( !spec.isValid() )
        {
            return;
        }

        TypeElement typeElement = spec.getTypeElement();
        ImplicitClass implicitClass = typeElement.getAnnotation(ImplicitClass.class);
        String packageName = getPackage(typeElement);
        ClassName templateQualifiedClassName = ClassName.get(packageName, typeElement.getSimpleName().toString());
        ClassName implicitQualifiedClassName = ClassName.get(packageName, getCaseClassSimpleName(typeElement, implicitClass.suffix(), implicitClass.unsuffix()));

        log("Generating ImplicitClass for " + templateQualifiedClassName + " as " + implicitQualifiedClassName);

        TypeSpec.Builder builder = TypeSpec.classBuilder(implicitQualifiedClassName)
            .superclass(ClassName.get(typeElement))
            .addModifiers(typeElement.getModifiers().toArray(new Modifier[typeElement.getModifiers().size()]))
        ;

        spec.getItems().forEach(item -> {
            if ( item.isValid() )
            {
                templates.addItem(builder, item.getExecutableElement());
            }
        });

        createSourceFile(packageName, templateQualifiedClassName, implicitQualifiedClassName, ImplicitClass.class.getSimpleName(), builder, typeElement);
    }
}

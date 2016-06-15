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
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import io.soabase.halva.implicit.Implicit;
import io.soabase.halva.implicit.ImplicitClass;
import io.soabase.halva.implicit.ImplicitContext;
import io.soabase.halva.processor.AnnotationReader;
import io.soabase.halva.processor.ProcessorBase;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes({"io.soabase.halva.implicit.ImplicitClass", "io.soabase.halva.implicit.ImplicitContext"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ImplicitClassProcessor extends ProcessorBase<ImplicitPairSpec, Templates>
{
    private static final ImplicitItem error = new ImplicitItem();
    private static final ImplicitItem ignore = new ImplicitItem();
    private static final ContextItem ignoreContext = new ContextItem();

    @Override
    protected Collection<? extends TypeElement> sort(Set<? extends TypeElement> annotations)
    {
        return annotations.stream()
            .sorted((a1, a2) -> a1.getSimpleName().toString().equals("ImplicitContext") ? -1 : 0) // ImplicitContexts first
            .collect(Collectors.toList());
    }

    @Override
    protected ImplicitPairSpec getItems(Templates templates, AnnotationReader annotationReader, Element element)
    {
        if ( annotationReader.getName().equals(ImplicitContext.class.getSimpleName()) )
        {
            return new ImplicitPairSpec(getImplicitContextItems(templates, element, annotationReader));
        }

        return new ImplicitPairSpec(getImplicitClassItems(element));
    }

    private ContextSpec getImplicitContextItems(Templates templates, Element element, AnnotationReader annotationReader)
    {
        List<ContextItem> items = element.getEnclosedElements().stream()
            .map(child -> {
                if ( child.getAnnotation(Implicit.class) != null )
                {
                    if ( !child.getModifiers().contains(Modifier.PUBLIC) && !child.getModifiers().contains(Modifier.STATIC) )
                    {
                        error(element, "@Implicit providers must be public and static");
                    }
                    else if ( (child.getKind() != ElementKind.METHOD) && (child.getKind() != ElementKind.FIELD) )
                    {
                        error(element, "@Implicit providers must be either fields or methods");
                    }
                    else if ( (child.getKind() == ElementKind.METHOD) && !isValidProviderMethod((ExecutableElement)child) )
                    {
                        error(element, "@Implicit provider methods cannot contain non implicit parameters");
                    }
                    else
                    {
                        DeclaredType childType = (child.getKind() == ElementKind.METHOD) ? (DeclaredType)((ExecutableElement)child).getReturnType() : (DeclaredType)child.asType();
                        if ( childType.getTypeArguments().size() > 0 )
                        {
                            templates.getSpecificTypeMap(childType).put(childType, child);
                        }
                        return new ContextItem(child);
                    }
                }
                return ignoreContext;
            })
            .filter(ContextItem::isValid)
            .collect(Collectors.toList())
        ;
        return new ContextSpec((TypeElement)element, annotationReader, items);
    }

    private boolean isValidProviderMethod(ExecutableElement method)
    {
        return method.getParameters().stream().allMatch(e -> e.getAnnotation(Implicit.class) != null);
    }

    private ImplicitSpec getImplicitClassItems(Element element)
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
        return new Templates(processingEnv);
    }

    @Override
    protected void buildClass(List<ImplicitPairSpec> previousSpecs, Templates templates, AnnotationReader annotationReader, ImplicitPairSpec specPair)
    {
        if ( specPair.getImplicitSpec() != null )
        {
            buildImplicitClass(previousSpecs, templates, annotationReader, specPair.getImplicitSpec());
        }
    }

    private boolean contextApplies(ContextSpec contextSpec, List<TypeMirror> limitContexts, TypeElement typeElement)
    {
        if ( contextSpec == null )
        {
            return false;
        }

        if ( limitContexts.size() > 0 )
        {
            if ( !limitContexts.stream().anyMatch(limit -> processingEnv.getTypeUtils().isSameType(contextSpec.getAnnotatedElement().asType(), typeElement.asType())) )
            {
                return false;
            }
        }

        List<TypeMirror> limits = contextSpec.getAnnotationReader().getClasses("limits");
        if ( limits.size() > 0 )
        {
            if ( !limits.stream().anyMatch(limit -> processingEnv.getTypeUtils().isSameType(limit, typeElement.asType())) )
            {
                return false;
            }
        }
        List<TypeMirror> excludes = contextSpec.getAnnotationReader().getClasses("excludes");
        if ( excludes.size() > 0 )
        {
            if ( excludes.stream().anyMatch(exclude -> processingEnv.getTypeUtils().isSameType(exclude, typeElement.asType())) )
            {
                return false;
            }
        }

        return true;
    }

    private void buildImplicitClass(List<ImplicitPairSpec> previousSpecs, Templates templates, AnnotationReader annotationReader, ImplicitSpec spec)
    {
        if ( !spec.isValid() )
        {
            return;
        }

        TypeElement typeElement = spec.getAnnotatedElement();

        ContextSpec thisContextSpec = getImplicitContextItems(templates, typeElement, annotationReader);
        List<ContextSpec> specs = new ArrayList<>();
        specs.addAll(previousSpecs.stream()
            .filter(p -> contextApplies(p.getContextSpec(), annotationReader.getClasses("limitContexts"), spec.getAnnotatedElement()))
            .map(ImplicitPairSpec::getContextSpec)
            .collect(Collectors.toList())
        );
        specs.add(getImplicitContextItems(templates, typeElement, annotationReader));

        Optional<? extends AnnotationMirror> implicitClassMirror = typeElement.getAnnotationMirrors().stream().filter(mirror -> mirror.getAnnotationType().toString().equals(ImplicitClass.class.getName())).findFirst();
        ImplicitClass implicitClass = typeElement.getAnnotation(ImplicitClass.class);
        String packageName = getPackage(typeElement);
        ClassName templateQualifiedClassName = ClassName.get(packageName, typeElement.getSimpleName().toString());
        ClassName implicitQualifiedClassName = ClassName.get(packageName, getCaseClassSimpleName(typeElement, implicitClass.suffix(), implicitClass.unsuffix()));

        log("Generating ImplicitClass for " + templateQualifiedClassName + " as " + implicitQualifiedClassName);

        List<Modifier> modifiers = typeElement.getModifiers().stream().filter(m -> m != Modifier.STATIC).collect(Collectors.toList());
        TypeSpec.Builder builder = TypeSpec.classBuilder(implicitQualifiedClassName)
            .addModifiers(modifiers.toArray(new Modifier[modifiers.size()]))
        ;
        annotationReader.getClasses("implicitInterfaces").forEach(clazz -> templates.addImplicitInterface(builder, clazz, specs));

        Optional<List<TypeVariableName>> typeVariableNames = addTypeVariableNames(builder, spec.getAnnotatedElement());
        if ( typeVariableNames.isPresent() )
        {
            builder.superclass(ParameterizedTypeName.get(ClassName.get(typeElement), typeVariableNames.get().toArray(new TypeName[typeVariableNames.get().size()])));
        }
        else
        {
            builder.superclass(ClassName.get(typeElement));
        }

        spec.getItems().forEach(item -> {
            if ( item.isValid() )
            {
                templates.addItem(builder, item.getExecutableElement(), specs);
            }
        });

        createSourceFile(packageName, templateQualifiedClassName, implicitQualifiedClassName, ImplicitClass.class.getSimpleName(), builder, typeElement);
    }
}

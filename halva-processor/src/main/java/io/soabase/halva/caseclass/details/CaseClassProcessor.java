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
package io.soabase.halva.caseclass.details;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import io.soabase.halva.caseclass.CaseClass;
import io.soabase.halva.caseclass.CaseClassIgnore;
import io.soabase.halva.caseclass.CaseClassMutable;
import io.soabase.halva.caseclass.CaseObject;
import io.soabase.halva.general.ProcessorBase;
import io.soabase.halva.tuple.ClassTuplable;
import io.soabase.halva.tuple.Tuplable;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("io.soabase.halva.caseclass.CaseClass")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CaseClassProcessor extends ProcessorBase<CaseClassSpec, Templates>
{
    private static final CaseClassItem errorItem = new CaseClassItem();
    private static final CaseClassItem ignoreItem = new CaseClassItem();

    public CaseClassProcessor()
    {
        super(CaseClass.class, CaseObject.class);
    }

    @Override
    protected CaseClassSpec getItems(Element element)
    {
        CaseClassSpec spec = new CaseClassSpec();
        do
        {
            if ( element.getKind() != ElementKind.INTERFACE )
            {
                error(element, "@CaseClass can only be applied to interfaces");
                break;
            }

            TypeElement typeElement = (TypeElement)element;
            InternalCaseClass caseClass = getInternalCaseClass(typeElement);
            List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
            List<CaseClassItem> items = enclosedElements.stream()
                .map(child -> {
                    if ( child.getKind() != ElementKind.METHOD )
                    {
                        return ignoreItem;
                    }

                    ExecutableElement executable = (ExecutableElement)child;
                    if ( (executable.getParameters().size() > 0) || (executable.getAnnotation(CaseClassIgnore.class) != null) )
                    {
                        if ( executable.isDefault() )
                        {
                            return ignoreItem;
                        }
                        error(element, "Non-CaseClass/CaseObject methods must have a default implementation");
                        return errorItem;
                    }

                    boolean mutable = (executable.getAnnotation(CaseClassMutable.class) != null);
                    if ( caseClass.isObject() )
                    {
                        if ( mutable )
                        {
                            error(element, "@CaseClassMutable cannot be used with @CaseObject");
                            return errorItem;
                        }
                        return ignoreItem;
                    }
                    else
                    {
                        if ( executable.getReturnType().getKind() == TypeKind.VOID )
                        {
                            error(element, "@CaseClass/CaseObject methods cannot return void");
                            return errorItem;
                        }
                    }

                    return new CaseClassItem(executable.getSimpleName().toString(), executable.getReturnType(), processingEnv.getTypeUtils().erasure(executable.getReturnType()), executable.isDefault(), mutable);
                })
                .filter(item -> item != ignoreItem)
                .collect(Collectors.toList());
            if ( items.contains(errorItem) )
            {
                break;
            }

            spec = new CaseClassSpec(typeElement, items);
        } while ( false );

        return spec;
    }

    @Override
    protected void buildClass(Templates templates, CaseClassSpec spec)
    {
        InternalCaseClass caseClass = getInternalCaseClass(spec.getElement());
        String packageName = getPackage(spec.getElement());
        ClassName originalQualifiedClassName = ClassName.get(packageName, spec.getElement().getSimpleName().toString());
        ClassName qualifiedClassName = ClassName.get(packageName, getCaseClassSimpleName(spec, caseClass));

        String annotationType = caseClass.getType();
        log("Generating " + annotationType + " for " + originalQualifiedClassName + " as " + qualifiedClassName);

        Collection<Modifier> modifiers = getModifiers(spec.getElement());

        TypeName baseType = TypeName.get(spec.getElement().asType());
        TypeSpec.Builder builder = TypeSpec.classBuilder(qualifiedClassName)
            .addSuperinterface(baseType)
            .addSuperinterface(Serializable.class)
            .addSuperinterface(Tuplable.class)
            .addSuperinterface(ClassTuplable.class)
            .addModifiers(modifiers.toArray(new Modifier[modifiers.size()]));

        Optional<List<TypeVariableName>> typeVariableNames;
        if ( spec.getElement().getTypeParameters().size() > 0 )
        {
            List<TypeVariableName> localTypeVariableNames = spec.getElement().getTypeParameters().stream()
                .map(TypeVariableName::get)
                .collect(Collectors.toList());
            builder.addTypeVariables(localTypeVariableNames);
            typeVariableNames = Optional.of(localTypeVariableNames);
        }
        else
        {
            typeVariableNames = Optional.empty();
        }

        if ( caseClass.json() )
        {
            AnnotationSpec annotationSpec = AnnotationSpec.builder(ClassName.get("com.fasterxml.jackson.databind.annotation", "JsonDeserialize"))
                .addMember("builder", "$T.class", templates.getBuilderClassName(qualifiedClassName, typeVariableNames))
                .build();
            builder.addAnnotation(annotationSpec);
        }
        spec.getItems().forEach(item -> templates.addItem(item, builder, caseClass.json()));
        templates.addConstructor(spec, builder, caseClass.isObject());
        if ( caseClass.isObject() )
        {
            templates.addObjectInstance(builder, qualifiedClassName, typeVariableNames);
        }
        else
        {
            templates.addBuilder(spec, builder, qualifiedClassName, caseClass.json(), typeVariableNames);
            templates.addCopy(builder, qualifiedClassName, typeVariableNames);
            templates.addApplyBuilder(spec, builder, qualifiedClassName, typeVariableNames);
        }
        templates.addEquals(spec, builder, qualifiedClassName);
        templates.addTuple(spec, builder);
        templates.addHashCode(spec, builder);
        templates.addDebugString(spec, builder, qualifiedClassName);
        templates.addToString(spec, builder, qualifiedClassName);
        templates.addClassTuple(spec, builder, qualifiedClassName, caseClass.json());

        createSourceFile(packageName, originalQualifiedClassName, qualifiedClassName, annotationType, builder, spec.getElement());
    }

    @Override
    protected Templates newTemplates()
    {
        return new Templates(processingEnv);
    }

    private InternalCaseClass getInternalCaseClass(TypeElement element)
    {
        CaseClass caseClass = element.getAnnotation(CaseClass.class);
        if ( caseClass != null )
        {
            return new InternalCaseClass(caseClass);
        }
        return new InternalCaseClass(element.getAnnotation(CaseObject.class));
    }

    private String getCaseClassSimpleName(CaseClassSpec spec, InternalCaseClass caseClass)
    {
        return getCaseClassSimpleName(spec.getElement(), caseClass.suffix(), caseClass.unsuffix());
    }
}

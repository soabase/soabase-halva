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

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import io.soabase.halva.caseclass.CaseObject;
import io.soabase.halva.processor.Environment;
import io.soabase.halva.processor.GeneratedClass;
import io.soabase.halva.processor.Pass;
import io.soabase.halva.tuple.ClassTuplable;
import io.soabase.halva.tuple.Tuplable;
import javax.lang.model.element.Modifier;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

class PassCreateClass implements Pass
{
    private final Environment environment;
    private final List<CaseClassSpec> specs;
    private final Templates templates;

    PassCreateClass(Environment environment, List<CaseClassSpec> specs)
    {
        this.environment = environment;
        this.specs = specs;
        templates = new Templates(environment);
    }

    @Override
    public Optional<Pass> process()
    {
        specs.forEach(this::processOneSpec);
        return Optional.empty();
    }

    private void processOneSpec(CaseClassSpec spec)
    {
        String packageName = environment.getPackage(spec.getAnnotatedElement());
        GeneratedClass generatedClass = environment.getGeneratedManager().resolve(spec.getAnnotatedElement());
        boolean isCaseObject = spec.getAnnotationReader().getName().equals(CaseObject.class.getSimpleName());

        environment.log("Generating " + spec.getAnnotationReader().getName() + " for " + generatedClass.getOriginal() + " as " + generatedClass.getGenerated());

        Collection<Modifier> modifiers = environment.getModifiers(spec.getAnnotatedElement());
        TypeName baseType = TypeName.get(spec.getAnnotatedElement().asType());
        boolean asEnum = isCaseObject && spec.getAnnotationReader().getBoolean("asEnum");
        TypeSpec.Builder builder;
        if ( asEnum )
        {
            builder = TypeSpec.enumBuilder(generatedClass.getGenerated())
                .addEnumConstant(generatedClass.getGenerated().simpleName());
        }
        else
        {
            builder = TypeSpec.classBuilder(generatedClass.getGenerated())
                .addSuperinterface(Serializable.class);
        }
        builder.addSuperinterface(baseType)
            .addSuperinterface(Tuplable.class)
            .addSuperinterface(ClassTuplable.class)
            .addModifiers(modifiers.toArray(new Modifier[modifiers.size()]));

        Optional<List<TypeVariableName>> typeVariableNames = environment.addTypeVariableNames(builder::addTypeVariables, spec.getAnnotatedElement().getTypeParameters());
        boolean json = spec.getAnnotationReader().getBoolean("json");
        if ( json )
        {
            AnnotationSpec annotationSpec = AnnotationSpec.builder(ClassName.get("com.fasterxml.jackson.databind.annotation", "JsonDeserialize"))
                .addMember("builder", "$T.class", templates.getBuilderClassName(generatedClass.getGenerated(), typeVariableNames))
                .build();
            builder.addAnnotation(annotationSpec);
        }
        spec.getItems().forEach(item -> templates.addItem(item, builder, json));
        if ( isCaseObject )
        {
            if ( !asEnum )
            {
                templates.addObjectInstance(builder, generatedClass.getGenerated(), typeVariableNames);
            }
        }
        else
        {
            templates.addBuilder(spec, builder, generatedClass.getGenerated(), json, typeVariableNames);
            templates.addCopy(builder, generatedClass.getGenerated(), typeVariableNames);
            templates.addApplyBuilder(spec, builder, generatedClass.getGenerated(), typeVariableNames);
        }
        if ( !asEnum )
        {
            templates.addConstructor(spec, builder);
            templates.addEquals(spec, builder, generatedClass.getGenerated());
            templates.addHashCode(spec, builder);
        }
        templates.addTuple(spec, builder);
        templates.addDebugString(spec, builder, generatedClass.getGenerated());
        templates.addToString(spec, builder, generatedClass.getGenerated());
        templates.addClassTupleMethods(spec, builder, generatedClass.getGenerated(), typeVariableNames);
        templates.addClassTuple(spec, builder, generatedClass.getGenerated(), json);

        environment.createSourceFile(packageName, generatedClass.getOriginal(), generatedClass.getGenerated(), spec.getAnnotationReader().getFullName(), builder, spec.getAnnotatedElement());
    }
}

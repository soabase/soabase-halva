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
package io.soabase.halva.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import io.soabase.halva.processor.container.ContainerManager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public interface Environment
{
    void error(Element element, String message);

    void log(String message);

    Elements getElementUtils();

    Types getTypeUtils();

    String getPackage(TypeElement element);

    Collection<Modifier> getModifiers(TypeElement element);

    void createSourceFile(String packageName, ClassName templateQualifiedClassName, ClassName generatedQualifiedClassName, String annotationType, TypeSpec.Builder builder, TypeElement element);

    Optional<List<TypeVariableName>> addTypeVariableNames(Consumer<List<TypeVariableName>> applier, List<? extends TypeParameterElement> elements);

    DeclaredType typeOfFieldOrMethod(Element element);

    TypeMirror getResolvedReturnType(ExecutableElement method, DeclaredType enclosing);

    ContainerManager getContainerManager();

    GeneratedManager getGeneratedManager();
}

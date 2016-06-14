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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import io.soabase.halva.alias.TypeAlias;
import io.soabase.halva.processor.ProcessorBase;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SupportedAnnotationTypes("io.soabase.halva.alias.TypeAlias")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class TypeAliasProcessor extends ProcessorBase<AliasSpec, Templates>
{
    public TypeAliasProcessor()
    {
        super(TypeAlias.class);
    }

    @Override
    protected AliasSpec getItems(Element element)
    {
        //noinspection LoopStatementThatDoesntLoop
        do
        {
            if ( element.getKind() != ElementKind.INTERFACE )
            {
                error(element, "@TypeAlias can only be applied to interfaces");
                break;
            }

            TypeElement typeElement = (TypeElement)element;
            if ( typeElement.getInterfaces().size() != 1 )
            {
                error(element, "@TypeAlias interfaces must extend a type that is to be aliased");
            }
            TypeMirror parentType = typeElement.getInterfaces().get(0);

            if ( typeElement.getEnclosedElements().size() != 0 )
            {
                error(element, "@TypeAlias must be completely empty");
                break;
            }

            return new AliasSpec(typeElement, (DeclaredType)parentType);
        } while ( false );

        return new AliasSpec();
    }

    @Override
    protected void buildClass(Templates templates, AliasSpec spec)
    {
        if ( !spec.isValid() )
        {
            return;
        }

        TypeElement typeElement = spec.getElement();
        TypeAlias typeAlias = typeElement.getAnnotation(TypeAlias.class);
        String packageName = getPackage(typeElement);
        ClassName templateQualifiedClassName = ClassName.get(packageName, typeElement.getSimpleName().toString());
        ClassName aliasQualifiedClassName = ClassName.get(packageName, getCaseClassSimpleName(typeElement, typeAlias.suffix(), typeAlias.unsuffix()));

        log("Generating TypeAlias for " + templateQualifiedClassName + " as " + aliasQualifiedClassName);

        Collection<Modifier> modifiers = getModifiers(typeElement);
        TypeName baseTypeName = ClassName.get(spec.getParameterizedType());

        TypeSpec.Builder builder = TypeSpec.interfaceBuilder(aliasQualifiedClassName)
            .addSuperinterface(baseTypeName)
            .addModifiers(modifiers.toArray(new Modifier[modifiers.size()]));

        templates.addTypeAliasType(builder, aliasQualifiedClassName, spec.getParameterizedType());
        templates.addDelegation(builder, aliasQualifiedClassName, spec.getParameterizedType());

        createSourceFile(packageName, templateQualifiedClassName, aliasQualifiedClassName, TypeAlias.class.getSimpleName(), builder, typeElement);
    }

    @Override
    protected Templates newTemplates()
    {
        return new Templates(processingEnv);
    }

    private boolean isSameUnderlyingType(List<? extends TypeMirror> lhs, List<? extends TypeMirror> rhs)
    {
        return String.valueOf(lhs).equals(String.valueOf(rhs));
    }

    private List<? extends TypeMirror> getUnderlyingType(TypeMirror type)
    {
        if ( type.getKind() == TypeKind.DECLARED )
        {
            DeclaredType declaredType = (DeclaredType)type;
            List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
            if ( typeArguments != null )
            {
                return typeArguments;
            }
        }
        return new ArrayList<>();
    }
}

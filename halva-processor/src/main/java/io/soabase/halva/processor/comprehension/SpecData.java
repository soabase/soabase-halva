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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;
import io.soabase.halva.any.AnyVal;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class SpecData
{
    final List<TypeVariableName> typeVariableNames;
    final TypeName parameterizedMonadicName;
    final ParameterizedTypeName anyName;
    final TypeName monadicTypeName;

    SpecData(MonadicSpec spec)
    {
        typeVariableNames = IntStream.range(0, spec.getMonadElement().getTypeParameters().size())
            .mapToObj(i -> TypeVariableName.get("MF_" + getIndexedName(i)))
            .collect(Collectors.toList());
        int monadicParameterPosition = spec.getAnnotationReader().getInt("monadicParameterPosition");
        if ( (monadicParameterPosition < 0) || (monadicParameterPosition >= typeVariableNames.size()) )
        {
            monadicParameterPosition = typeVariableNames.size() - 1;
        }
        monadicTypeName = typeVariableNames.get(monadicParameterPosition);

        List<? extends TypeMirror> monadTypeArguments = spec.getMonadType().getTypeArguments();
        TypeMirror monadicParameterType = (monadTypeArguments.size() > monadicParameterPosition) ? monadTypeArguments.get(monadicParameterPosition) : null;
        if ( spec.getAnnotationReader().getBoolean("applyParentTypeParameter") && (monadicParameterType != null) && (monadicParameterType instanceof DeclaredType) )
        {
            DeclaredType declaredType = (DeclaredType)monadicParameterType;
            TypeName[] typeNames = new TypeName[typeVariableNames.size()];
            for ( int i = 0; i < (typeVariableNames.size() - 1); ++i )
            {
                typeNames[i] = typeVariableNames.get(i);
            }
            typeNames[typeVariableNames.size() - 1] = ParameterizedTypeName.get(ClassName.get((TypeElement)declaredType.asElement()), typeVariableNames.get(typeVariableNames.size() - 1));
            parameterizedMonadicName = ParameterizedTypeName.get(ClassName.get(spec.getMonadElement()), typeNames);
        }
        else
        {
            parameterizedMonadicName = ParameterizedTypeName.get(ClassName.get(spec.getMonadElement()), typeVariableNames.toArray(new TypeName[typeVariableNames.size()]));
        }

        anyName = ParameterizedTypeName.get(ClassName.get(AnyVal.class), monadicTypeName);
    }

    private String getIndexedName(int i)
    {
        return Character.toString((char)('A' + i));
    }
}

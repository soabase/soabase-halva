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
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import io.soabase.halva.any.AnyDeclaration;
import io.soabase.halva.any.AnyType;
import io.soabase.halva.implicit.Implicit;
import io.soabase.halva.implicit.Implicits;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import java.util.concurrent.atomic.AtomicBoolean;

class Templates
{
    void addItem(TypeSpec.Builder builder, ExecutableElement element)
    {
        ClassName implicitsClassName = ClassName.get(Implicits.class);
        ClassName anyTypeClassName = ClassName.get(AnyType.class);
        ClassName anyDeclarationClassName = ClassName.get(AnyDeclaration.class);

        MethodSpec.Builder methodSpecBuilder = (element.getKind() == ElementKind.CONSTRUCTOR) ? MethodSpec.constructorBuilder() : MethodSpec.methodBuilder(element.getSimpleName().toString());
        methodSpecBuilder.addModifiers(element.getModifiers());
        if ( element.getReturnType().getKind() != TypeKind.VOID )
        {
            methodSpecBuilder.returns(ClassName.get(element.getReturnType()));
        }

        CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();
        if ( element.getKind() == ElementKind.CONSTRUCTOR )
        {
            codeBlockBuilder.add("super(");
        }
        else if ( element.getReturnType().getKind() == TypeKind.VOID )
        {
            codeBlockBuilder.add("super.$L(", element.getSimpleName());
        }
        else
        {
            codeBlockBuilder.add("return super.$L(", element.getSimpleName());
        }
        AtomicBoolean isFirst = new AtomicBoolean(false);
        element.getParameters().forEach(parameter -> {
            if ( !isFirst.compareAndSet(false, true) )
            {
                codeBlockBuilder.add(", ");
            }
            if ( parameter.getAnnotation(Implicit.class) != null )
            {
                if ( ((DeclaredType)parameter.asType()).getTypeArguments().size() > 0 )
                {
                    codeBlockBuilder.add("$T.Implicits().getValue($T.of(new $T<$T>(){}))", implicitsClassName, anyDeclarationClassName, anyTypeClassName, parameter.asType());
                }
                else
                {
                    codeBlockBuilder.add("$T.Implicits().getValue($T.of($T.class))", implicitsClassName, anyDeclarationClassName, parameter.asType());
                }
            }
            else
            {
                ParameterSpec.Builder parameterSpec = ParameterSpec.builder(ClassName.get(parameter.asType()), parameter.getSimpleName().toString(), parameter.getModifiers().toArray(new javax.lang.model.element.Modifier[parameter.getModifiers().size()]));
                methodSpecBuilder.addParameter(parameterSpec.build());
                codeBlockBuilder.add(parameter.getSimpleName().toString());
            }
        });

        methodSpecBuilder.addCode(codeBlockBuilder.add(");\n").build());
        builder.addMethod(methodSpecBuilder.build());
    }
}

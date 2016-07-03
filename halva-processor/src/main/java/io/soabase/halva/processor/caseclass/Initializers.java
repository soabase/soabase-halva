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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import io.soabase.halva.processor.Environment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class Initializers
{
    private final Environment environment;

    private static class FormattedClassName
    {
        final String format;
        final ClassName className;
        final Applier applier;

        FormattedClassName(String format, ClassName className, Applier applier)
        {
            this.format = format;
            this.className = className;
            this.applier = applier;
        }
    }

    interface Applier
    {
        void apply(CodeBlock.Builder builder, FormattedClassName special, CaseClassItem item);
    }

    private static void applyStandard(CodeBlock.Builder builder, FormattedClassName special, CaseClassItem item)
    {
        builder.addStatement(special.format, item.getName());
    }

    private static void applyConcurrentMap(CodeBlock.Builder builder, FormattedClassName special, CaseClassItem item)
    {
        builder.addStatement(special.format, item.getName(), special.className);
    }

    private static void applyCollections(CodeBlock.Builder builder, FormattedClassName special, CaseClassItem item)
    {
        builder.addStatement(special.format, item.getName(), collectionsClassName);
    }

    private static final ClassName collectionsClassName = ClassName.get("java.util", "Collections");
    private static final Map<String, FormattedClassName> specials;
    static
    {
        Map<String, FormattedClassName> worker = new HashMap<>();
        worker.put("java.util.List", new FormattedClassName("$L = $T.emptyList()", null, Initializers::applyCollections));
        worker.put("java.util.Map", new FormattedClassName("$L = $T.emptyMap()", null, Initializers::applyCollections));
        worker.put("java.util.Set", new FormattedClassName("$L = $T.emptySet()", null, Initializers::applyCollections));
        worker.put("java.util.Collection", new FormattedClassName("$L = $T.emptySet()", null, Initializers::applyCollections));
        worker.put("java.util.concurrent.ConcurrentMap", new FormattedClassName("$L = new $T<>()", ClassName.get("java.util.concurrent", "ConcurrentHashMap"), Initializers::applyConcurrentMap));
        worker.put("java.lang.String", new FormattedClassName("$L = \"\"", ClassName.get("java.lang", "String"), Initializers::applyStandard));
        worker.put("java.util.Optional", new FormattedClassName("$L = Optional.empty()", ClassName.get("java.util", "Optional"), Initializers::applyStandard));
        specials = Collections.unmodifiableMap(worker);
    }

    Initializers(Environment environment)
    {
        this.environment = environment;
    }

    void addTo(CodeBlock.Builder codeBuilder, CaseClassSpec spec, CaseClassItem item)
    {
        if ( !item.getType().getKind().isPrimitive() || item.hasDefaultValue() )
        {
            CodeBlock.Builder builder = codeBuilder.beginControlFlow("if ( $L == null )", item.getName());
            if ( item.hasDefaultValue() )
            {
                builder.addStatement("$L = $L.super.$L()", item.getName(), spec.getAnnotatedElement().getSimpleName(), item.getName());
            }
            else do
            {
                if ( item.getType().getKind() == TypeKind.ARRAY )
                {
                    addArray(item, builder);
                    break;
                }

                try
                {
                    PrimitiveType unboxedType = environment.getTypeUtils().unboxedType(item.getType());
                    builder.addStatement("$L = $L", item.getName(), (unboxedType.getKind() == TypeKind.BOOLEAN) ? "false" : "0");
                    break;
                }
                catch ( IllegalArgumentException ignore )
                {
                    // ignore
                }

                String erasedTypeName = environment.getTypeUtils().erasure(item.getType()).toString();
                FormattedClassName special = specials.get(erasedTypeName);
                if ( special != null )
                {
                    special.applier.apply(builder, special, item);
                }
                else
                {
                    Element element = environment.getTypeUtils().asElement(item.getType());
                    if ( hasNoArgConstructor(element) )
                    {
                        builder.addStatement("$L = new $L()", item.getName(), element.getSimpleName());
                    }
                    else
                    {
                        builder.addStatement("throw new IllegalArgumentException(\"\\\"$L\\\" does not have a default value\")", item.getName());
                    }
                }
            } while ( false );
            codeBuilder.endControlFlow();
        }
    }

    private void addArray(CaseClassItem item, CodeBlock.Builder builder)
    {
        ArrayType arrayType = (ArrayType)item.getType();
        StringBuilder str = new StringBuilder("[0]");
        while ( arrayType.getComponentType().getKind() == TypeKind.ARRAY )
        {
            str.append("[]");
            arrayType = (ArrayType)arrayType.getComponentType();
        }
        builder.addStatement("$L = new $T$L", item.getName(), arrayType.getComponentType(), str.toString());
    }

    private boolean hasNoArgConstructor(Element element)
    {
        return element.getEnclosedElements().stream().anyMatch(child ->
            (child.getKind() == ElementKind.CONSTRUCTOR)
            && child.getModifiers().contains(Modifier.PUBLIC)
            && (((ExecutableElement)child).getParameters().size() == 0)
        );
    }
}

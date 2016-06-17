package io.soabase.halva.processor.implicit2;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import io.soabase.halva.implicit.Implicit;
import io.soabase.halva.processor.Environment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

class ImplicitValue
{
    private final Environment environment;
    private final List<ContextItem> contextItems;
    private final FoundImplicit foundImplicit;

    ImplicitValue(Environment environment, List<ContextItem> contextItems, FoundImplicit foundImplicit)
    {
        this.environment = environment;
        this.contextItems = contextItems;
        this.foundImplicit = foundImplicit;
    }

    CodeBlock build()
    {
        CodeBlock.Builder builder = CodeBlock.builder();
        if ( foundImplicit != null )
        {
            if ( foundImplicit.getElement().isPresent() )
            {
                addDirectValue(builder);
            }
            else
            {
                // TODO
            }
        }
        else
        {
            builder.add("null");
        }
        return builder.build();
    }

    private void addDirectValue(CodeBlock.Builder builder)
    {
        Element element = foundImplicit.getElement().get();
        if ( element.getKind() == ElementKind.FIELD )
        {
            builder.add("$T.$L", element.getEnclosingElement().asType(), element.getSimpleName());
        }
        else
        {
            ExecutableElement method = (ExecutableElement)element;
            AtomicBoolean isFirst = new AtomicBoolean(false);
            CodeBlock methodCode = new ImplicitMethod(environment, method, contextItems).build();
            builder.add("$T.$L(", element.getEnclosingElement().asType(), element.getSimpleName());
            builder.add(methodCode);
            builder.add(")");
        }
    }
}

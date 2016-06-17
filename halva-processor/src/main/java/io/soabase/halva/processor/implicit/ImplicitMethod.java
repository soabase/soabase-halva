package io.soabase.halva.processor.implicit;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import io.soabase.halva.implicit.Implicit;
import io.soabase.halva.processor.Environment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

class ImplicitMethod
{
    private final Environment environment;
    private final ExecutableElement method;
    private final List<ContextItem> contextItems;

    ImplicitMethod(Environment environment, ExecutableElement method, List<ContextItem> contextItems)
    {
        this.environment = environment;
        this.method = method;
        this.contextItems = contextItems;
    }

    CodeBlock build()
    {
        return build(p -> {});
    }

    CodeBlock build(Consumer<VariableElement> parameterAdded)
    {
        CodeBlock.Builder builder = CodeBlock.builder();
        AtomicBoolean isFirst = new AtomicBoolean(false);
        method.getParameters().forEach(parameter -> {
            if ( !isFirst.compareAndSet(false, true) )
            {
                builder.add(", ");
            }
            if ( parameter.getAnnotation(Implicit.class) != null )
            {
                FoundImplicit foundImplicit = new ImplicitSearcher(environment, contextItems).find(parameter.asType());
                CodeBlock value = new ImplicitValue(environment, contextItems, foundImplicit).build();
                builder.add(value);
            }
            else
            {
                parameterAdded.accept(parameter);
                builder.add(parameter.getSimpleName().toString());
            }
        });
        return builder.build();
    }
}

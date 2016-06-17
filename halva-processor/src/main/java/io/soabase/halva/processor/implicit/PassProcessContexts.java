package io.soabase.halva.processor.implicit;

import io.soabase.halva.implicit.Implicit;
import io.soabase.halva.implicit.ImplicitContext;
import io.soabase.halva.processor.Environment;
import io.soabase.halva.processor.Pass;
import io.soabase.halva.processor.WorkItem;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class PassProcessContexts implements Pass
{
    private final Environment environment;
    private final List<WorkItem> workItems;

    PassProcessContexts(Environment environment, List<WorkItem> workItems)
    {
        this.environment = environment;
        this.workItems = workItems;
    }

    @Override
    public Optional<Pass> process()
    {
        List<WorkItem> implicitContextItems = new ArrayList<>();
        List<WorkItem> implicitClassItems = new ArrayList<>();
        workItems.forEach(item -> {
            if ( item.getAnnotationReader().getName().equals(ImplicitContext.class.getSimpleName()) )
            {
                implicitContextItems.add(item);
            }
            else
            {
                implicitClassItems.add(item);
            }
        });

        List<ContextItem> contextItems = processContextItems(implicitContextItems);
        return Optional.of(new PassProcessImplicits(environment, implicitClassItems, contextItems));
    }

    private List<ContextItem> processContextItems(List<WorkItem> implicitContextItems)
    {
        List<ContextItem> contextItems = new ArrayList<>();
        implicitContextItems.forEach(item -> {
            Element element = item.getElement();
            element.getEnclosedElements().forEach(child -> {
                if ( child.getAnnotation(Implicit.class) != null )
                {
                    if ( !child.getModifiers().contains(Modifier.PUBLIC) && !child.getModifiers().contains(Modifier.STATIC) )
                    {
                        environment.error(element, "@Implicit providers must be public and static");
                    }
                    else if ( (child.getKind() != ElementKind.METHOD) && (child.getKind() != ElementKind.FIELD) )
                    {
                        environment.error(element, "@Implicit providers must be either fields or methods");
                    }
                    else if ( (child.getKind() == ElementKind.METHOD) && !isValidProviderMethod((ExecutableElement)child) )
                    {
                        environment.error(element, "@Implicit provider methods cannot contain non implicit parameters");
                    }
                    else
                    {
                        DeclaredType childType = environment.typeOfFieldOrMethod(child);
                        contextItems.add(new ContextItem((TypeElement)element, item.getAnnotationReader(), child, childType));
                    }
                }
            });
        });
        return contextItems;
    }

    private boolean isValidProviderMethod(ExecutableElement method)
    {
        return method.getParameters().stream().allMatch(e -> e.getAnnotation(Implicit.class) != null);
    }
}

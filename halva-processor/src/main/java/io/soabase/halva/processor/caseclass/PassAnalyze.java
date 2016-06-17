package io.soabase.halva.processor.caseclass;

import io.soabase.halva.caseclass.CaseClassIgnore;
import io.soabase.halva.caseclass.CaseClassMutable;
import io.soabase.halva.caseclass.CaseObject;
import io.soabase.halva.processor.Environment;
import io.soabase.halva.processor.Pass;
import io.soabase.halva.processor.WorkItem;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class PassAnalyze implements Pass
{
    private final Environment environment;
    private final List<WorkItem> workItems;

    PassAnalyze(Environment environment, List<WorkItem> workItems)
    {
        this.environment = environment;
        this.workItems = workItems;
    }

    @Override
    public Optional<Pass> process()
    {
        List<CaseClassSpec> specs = new ArrayList<>();
        workItems.forEach(item -> {
            Element element = item.getElement();
            do
            {
                if ( element.getKind() != ElementKind.INTERFACE )
                {
                    environment.error(element, "@CaseClass can only be applied to interfaces");
                    break;
                }

                List<CaseClassItem> caseClassItems = new ArrayList<>();
                TypeElement typeElement = (TypeElement)element;
                typeElement.getEnclosedElements().forEach(child -> {
                    do
                    {
                        if ( child.getKind() != ElementKind.METHOD )
                        {
                            break;
                        }

                        ExecutableElement executable = (ExecutableElement)child;
                        if ( (executable.getParameters().size() > 0) || (executable.getAnnotation(CaseClassIgnore.class) != null) )
                        {
                            if ( executable.isDefault() )
                            {
                                break;
                            }
                            environment.error(element, "Non-CaseClass/CaseObject methods must have a default implementation");
                            break;
                        }

                        boolean mutable = (executable.getAnnotation(CaseClassMutable.class) != null);
                        if ( item.getAnnotationReader().getName().equals(CaseObject.class.getSimpleName()) )
                        {
                            if ( mutable )
                            {
                                environment.error(element, "@CaseClassMutable cannot be used with @CaseObject");
                                break;
                            }
                            break;
                        }
                        else
                        {
                            if ( executable.getReturnType().getKind() == TypeKind.VOID )
                            {
                                environment.error(element, "@CaseClass/CaseObject methods cannot return void");
                                break;
                            }
                        }

                        CaseClassItem caseClassItem = new CaseClassItem(executable.getSimpleName().toString(), executable.getReturnType(), environment.getTypeUtils().erasure(executable.getReturnType()), executable.isDefault(), mutable);
                        caseClassItems.add(caseClassItem);
                    } while ( false );
                });

                specs.add(new CaseClassSpec(typeElement, item.getAnnotationReader(), caseClassItems));
            } while ( false );
        });
        return Optional.of(new PassCreateClass(environment, specs));
    }
}

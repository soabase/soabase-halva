package io.soabase.halva.processor.caseclass;

import io.soabase.halva.processor.AnnotationReader;
import javax.lang.model.element.TypeElement;
import java.util.List;

class CaseClassSpec
{
    private final TypeElement element;
    private final AnnotationReader annotationReader;
    private final List<CaseClassItem> items;

    CaseClassSpec(TypeElement element, AnnotationReader annotationReader, List<CaseClassItem> items)
    {
        this.element = element;
        this.annotationReader = annotationReader;
        this.items = items;
    }

    AnnotationReader getAnnotationReader()
    {
        return annotationReader;
    }

    TypeElement getAnnotatedElement()
    {
        return element;
    }

    List<CaseClassItem> getItems()
    {
        return items;
    }
}

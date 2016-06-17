package io.soabase.halva.processor;

import javax.lang.model.element.Element;

public class WorkItem
{
    private final AnnotationReader annotationReader;
    private final Element element;

    public WorkItem(Element element, AnnotationReader annotationReader)
    {
        this.element = element;
        this.annotationReader = annotationReader;
    }

    public Element getElement()
    {
        return element;
    }

    public AnnotationReader getAnnotationReader()
    {
        return annotationReader;
    }
}

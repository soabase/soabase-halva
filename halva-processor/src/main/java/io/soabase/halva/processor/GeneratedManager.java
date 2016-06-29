package io.soabase.halva.processor;

import com.squareup.javapoet.ClassName;
import javax.lang.model.element.TypeElement;

public interface GeneratedManager
{
    void registerGenerated(TypeElement element, AnnotationReader annotationReader);

    ClassName resolve(ClassName original);
}

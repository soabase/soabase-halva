package io.soabase.halva.processor.implicit;

import io.soabase.halva.processor.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

@SupportedAnnotationTypes({"io.soabase.halva.implicit.ImplicitClass", "io.soabase.halva.implicit.ImplicitContext"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ImplicitClassProcessor extends Processor
{
    public ImplicitClassProcessor()
    {
        super(new ImplicitPassFactory());
    }
}

package io.soabase.halva.processor.caseclass;

import io.soabase.halva.processor.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

@SupportedAnnotationTypes({"io.soabase.halva.caseclass.CaseClass", "io.soabase.halva.caseclass.CaseObject"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CaseClassProcessor extends Processor
{
    public CaseClassProcessor()
    {
        super(new CaseClassPassFactory());
    }
}

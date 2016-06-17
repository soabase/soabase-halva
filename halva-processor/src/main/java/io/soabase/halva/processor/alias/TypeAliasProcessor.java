package io.soabase.halva.processor.alias;

import io.soabase.halva.processor.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

@SupportedAnnotationTypes("io.soabase.halva.alias.TypeAlias")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class TypeAliasProcessor extends Processor
{
    public TypeAliasProcessor()
    {
        super(new AliasPassFactory());
    }
}

package io.soabase.halva.caseclass;

import java.util.List;

@CaseClass
public interface WildCardType
{
    List<?> anyList();
}

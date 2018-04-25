package io.soabase.halva.caseclass;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@CaseClass(json = true, validate = true)
@JsonDeserialize(builder = CaseChildCase.Builder.class)
public interface CaseChild extends CaseParent
{
    @Override
    String parentValue();

    String childValue();
}

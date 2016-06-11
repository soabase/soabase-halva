package com.company;

import io.soabase.halva.caseclass.CaseClass;

@CaseClass
public interface GenericExample<A, B>
{
    A first();

    B second();
}

package com.company;

import io.soabase.halva.caseclass.CaseClass;

@CaseClass(json = true)
public interface JsonTest
{
    String firstName();
    String lastName();
    int age();
}

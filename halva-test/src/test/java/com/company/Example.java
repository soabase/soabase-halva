package com.company;

import io.soabase.halva.caseclass.CaseClass;
import java.util.Date;
import java.util.List;

@CaseClass
public interface Example
{
    String firstName();
    String lastName();
    int age();
    boolean active();
    List<Date> importantDates();
}

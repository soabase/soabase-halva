package com.company;

import io.soabase.halva.caseclass.CaseObject;
import java.util.Date;
import java.util.List;

import static io.soabase.halva.sugar.Sugar.List;

@CaseObject
public interface ExampleObject extends Example
{
    default String firstName()
    {
        return "John";
    }

    default String lastName()
    {
        return "Galt";
    }

    default int age()
    {
        return 42;
    }

    default boolean active()
    {
        return false;
    }

    default List<Date> importantDates()
    {
        return List();
    }
}

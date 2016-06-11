package com.company;

import io.soabase.halva.caseclass.CaseClass;
import io.soabase.halva.caseclass.CaseClassMutable;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

@CaseClass
public interface Test
{
    // you can have static constants, etc.
    int ONFIRE = 10;

    // defines a field for the case class
    String name();

    // defines a field with a default value
    default int date()
    {
        return 10;
    }

    List<Long> longList();

    Map<String, List<Date>> stringDateMap();

    ConcurrentMap<String, String> concurrentMap();

    boolean truth();

    Date activation();

    double percent();

    float section();

    Boolean isIt();

    // fields can also be mutable
    @CaseClassMutable
    String mutableValue();

    // Charset does not have a no-arg constructor. So it must be set in the builder
    Charset charset();

    // use default methods to add general use methods for the case class
    default void applyTo(Date date)
    {
        System.out.println(String.format("hey %s - %s", name(), date));
    }
}

package com.company;

import io.soabase.halva.implicit.Implicit;
import io.soabase.halva.implicit.ImplicitContext;
import java.util.List;
import java.util.function.Supplier;

@ImplicitContext
public class ExampleImplicitContext2
{
    @Implicit
    public static Supplier<List<String>> implicitPropertiesSupplier(@Implicit List<String> properties)
    {
        return () -> properties;
    }
}

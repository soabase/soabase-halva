package com.company;

import io.soabase.halva.implicit.Implicit;
import io.soabase.halva.implicit.ImplicitContext;
import java.util.List;
import java.util.function.Supplier;

import static io.soabase.halva.sugar.Sugar.List;

@ImplicitContext
public class ExampleImplicitContext
{
    @Implicit
    public static final List<String> implicitProperties = List("one", "two", "three");

    @Implicit
    public static Supplier<List<String>> implicitPropertiesSupplier(@Implicit List<String> properties)
    {
        return () -> properties;
    }
}

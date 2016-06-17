package com.company;

import io.soabase.halva.implicit.Implicit;
import io.soabase.halva.implicit.ImplicitContext;
import java.util.List;

import static io.soabase.halva.sugar.Sugar.List;

@ImplicitContext
public class ExampleImplicitContext1
{
    @Implicit
    public static final List<String> implicitProperties = List("one", "two", "three");
}

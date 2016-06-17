package com.company;

import io.soabase.halva.implicit.ImplicitClass;
import io.soabase.halva.implicit.Implicitly;
import java.util.List;
import java.util.function.Supplier;

@ImplicitClass
public class ExampleImplicitInterface implements Implicitly<Supplier<List<String>>>
{
}

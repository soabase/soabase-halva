package com.company;

import io.soabase.halva.implicit.Implicit;
import io.soabase.halva.implicit.ImplicitClass;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@ImplicitClass
public class ExampleImplicit
{
    private final List<String> properties;

    public ExampleImplicit(@Implicit Supplier<List<String>> propertiesProvider)
    {
        this.properties = Collections.unmodifiableList(new ArrayList<>(propertiesProvider.get()));
    }

    public List<String> getProperties()
    {
        return properties;
    }
}

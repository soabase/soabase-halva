// Auto generated from com.company.ExampleImplicitInterface by Soabase io.soabase.halva.implicit.ImplicitClass annotation processor
package com.company;

import java.lang.Override;
import java.lang.String;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Generated;

@Generated("io.soabase.halva.implicit.ImplicitClass")
public class ExampleImplicitInterfaceImpl extends ExampleImplicitInterface implements Supplier<List<String>> {
    public ExampleImplicitInterfaceImpl() {
        super();
    }

    @Override
    public List<String> get() {
        return ExampleImplicitContext2.implicitPropertiesSupplier(ExampleImplicitContext1.implicitProperties).get();
    }
}

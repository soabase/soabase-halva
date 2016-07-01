// Auto generated from com.company.OptionalForFactory by Soabase io.soabase.halva.comprehension.MonadicFor annotation processor
package com.company;

import io.soabase.halva.any.AnyVal;
import io.soabase.halva.comprehension.MonadicForImpl;
import java.lang.Boolean;
import java.lang.SuppressWarnings;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Generated;

@Generated("io.soabase.halva.comprehension.MonadicFor")
public class OptionalFor {
    private final MonadicForImpl<Optional> delegate;

    private OptionalFor(MonadicForImpl<Optional> delegate) {
        this.delegate = delegate;
    }

    @SuppressWarnings("unchecked")
    public static OptionalFor start() {
        return new OptionalFor(new MonadicForImpl<>(new OptionalForFactory()));
    }

    public <MF_A> OptionalFor forComp(AnyVal<MF_A> any, Supplier<? extends Optional<MF_A>> supplier) {
        delegate.forComp(any, supplier);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <MF_A> Optional<MF_A> yield(Supplier<MF_A> supplier) {
        return delegate.yield(supplier);
    }

    public <R> OptionalFor letComp(AnyVal<R> any, Supplier<R> supplier) {
        delegate.letComp(any, supplier);
        return this;
    }

    public OptionalFor filter(Supplier<Boolean> supplier) {
        delegate.filter(supplier);
        return this;
    }
}

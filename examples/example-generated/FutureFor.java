// Auto generated from com.company.FutureForFactory by Soabase io.soabase.halva.comprehension.MonadicFor annotation processor
package com.company;

import io.soabase.halva.any.AnyVal;
import io.soabase.halva.comprehension.MonadicForImpl;
import java.lang.SuppressWarnings;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import javax.annotation.Generated;

@Generated("io.soabase.halva.comprehension.MonadicFor")
public class FutureFor {
    private final MonadicForImpl<CompletableFuture> delegate;

    private FutureFor(MonadicForImpl<CompletableFuture> delegate) {
        this.delegate = delegate;
    }

    @SuppressWarnings("unchecked")
    public static FutureFor start() {
        return new FutureFor(new MonadicForImpl<>(new FutureForFactory()));
    }

    public <MF_A> FutureFor forComp(AnyVal<MF_A> any, Supplier<? extends CompletableFuture<MF_A>> supplier) {
        delegate.forComp(any, supplier);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <MF_A> CompletableFuture<MF_A> yield(Supplier<MF_A> supplier) {
        return delegate.yield(supplier);
    }

    public <R> FutureFor letComp(AnyVal<R> any, Supplier<R> supplier) {
        delegate.letComp(any, supplier);
        return this;
    }
}

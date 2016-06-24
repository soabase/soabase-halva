// Auto generated from io.soabase.halva.comprehension.StreamForFactory by Soabase MonadicFor annotation processor
package io.soabase.halva.comprehension;

import io.soabase.halva.any.AnyVal;
import java.lang.Boolean;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class StreamFor {
    private final MonadicForImpl<Stream> delegate;

    private StreamFor(MonadicForImpl<Stream> delegate) {
        this.delegate = delegate;
    }

    public static StreamFor start() {
        return new StreamFor(new MonadicForImpl<>(new StreamForFactory()));
    }

    public <A> StreamFor forComp(AnyVal<A> any, Supplier<? extends Stream<A>> supplier) {
        delegate.forComp(any, supplier);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <A> Stream<A> yield(Supplier<A> supplier) {
        return delegate.yield(supplier);
    }

    public <R> StreamFor letComp(AnyVal<R> any, Supplier<R> supplier) {
        delegate.letComp(any, supplier);
        return this;
    }

    public StreamFor filter(Supplier<Boolean> supplier) {
        delegate.filter(supplier);
        return this;
    }
}

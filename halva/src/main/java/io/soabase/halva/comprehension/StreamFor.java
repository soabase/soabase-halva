// Auto generated from io.soabase.halva.comprehension.StreamForFactory by Soabase io.soabase.halva.comprehension.MonadicFor annotation processor
package io.soabase.halva.comprehension;

import io.soabase.halva.any.Match;
import java.lang.Boolean;
import java.lang.SuppressWarnings;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Generated;

@Generated("io.soabase.halva.comprehension.MonadicFor")
public class StreamFor {
    private final MonadicForImpl<Stream> delegate;

    private StreamFor(MonadicForImpl<Stream> delegate) {
        this.delegate = delegate;
    }

    @SuppressWarnings("unchecked")
    public static StreamFor start() {
        return new StreamFor(new MonadicForImpl<>(new StreamForFactory()));
    }

    public <MF_A> StreamFor forComp(Match<MF_A> any, Supplier<? extends Stream<MF_A>> supplier) {
        delegate.forComp(any, supplier);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <MF_A> Stream<MF_A> yield(Supplier<MF_A> supplier) {
        return delegate.yield(supplier);
    }

    public <R> StreamFor letComp(Match<R> any, Supplier<R> supplier) {
        delegate.letComp(any, supplier);
        return this;
    }

    public StreamFor filter(Supplier<Boolean> supplier) {
        delegate.filter(supplier);
        return this;
    }
}

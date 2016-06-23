package io.soabase.halva.comprehension;

import io.soabase.halva.any.AnyVal;

import java.util.stream.Stream;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/** For comprehension working with Stream
 * Created by Alex on 6/22/2016.
 */
public class StreamFor {

    // Things that need to change

    public static class StreamForWrapper implements MonadicFor.MonadicForWrapper<Stream> {

        public <A> Stream flatMap(final Stream m, final Function<A, Stream> flat_mapper) {
            return m.flatMap(flat_mapper);
        }
        public <A, B> Stream map(final Stream m, final Function<A, B> mapper) {
            return m.map(mapper);
        }
        public <A, B> Stream filter(final Stream m, final Predicate<A> test) {
            return m.filter(test);
        }
    }
    public static <R> StreamFor forComp(final AnyVal<R> any, final Stream<R> starting_monad) {
        return new StreamFor(new MonadicForImpl(any, starting_monad, _wrapper));
    }
    public <R> StreamFor forComp(final AnyVal<R> any, final Supplier<Stream<R>> monad_supplier) {
        _delegate.forComp(any, (Supplier<Stream>)(Supplier<?>)monad_supplier);
        return this;
    }
    public <R> Stream<R> yield(Supplier<R> yield_supplier) {
        return _delegate.yield(yield_supplier);
    }

    // Things that can stay the same (can be templated/annotated)

    // External

    public <T> StreamFor letComp(final AnyVal<T> any, final Supplier<T> let_supplier) {
        _delegate.letComp(any, let_supplier);
        return this;
    }
    public StreamFor filter(final Supplier<Boolean> test) {
        _delegate.filter(test);
        return this;
    }

    // Internal

    private final MonadicFor<Stream> _delegate;
    private StreamFor(final MonadicFor<Stream> delegate) {
        _delegate = delegate;
    }
    private static final StreamForWrapper _wrapper = new StreamForWrapper();

}

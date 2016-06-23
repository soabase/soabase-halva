package io.soabase.halva.comprehension;

import io.soabase.halva.any.AnyVal;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

/** For comprehension working with CompletableFuture
 * Created by Alex on 6/22/2016.
 */
public class FutureFor {

    // Things that need to change

    public static class FutureForWrapper implements MonadicFor.MonadicForWrapper<CompletableFuture> {

        public <A> CompletableFuture flatMap(final CompletableFuture m, final Function<A, CompletableFuture> flat_mapper) {
            return m.thenCompose(flat_mapper);
        }
        public <A, B> CompletableFuture map(final CompletableFuture m, final Function<A, B> mapper) {
            return m.thenApply(mapper);
        }
    }
    public static <R> FutureFor forComp(final AnyVal<R> any, final CompletableFuture<R> starting_monad) {
        return new FutureFor(new MonadicForImpl(any, starting_monad, _wrapper));
    }
    public <R> FutureFor forComp(final AnyVal<R> any, final Supplier<CompletableFuture<R>> monad_supplier) {
        _delegate.forComp(any, (Supplier<CompletableFuture>)(Supplier<?>)monad_supplier);
        return this;
    }
    public <R> CompletableFuture<R> yield(Supplier<R> yield_supplier) {
        return _delegate.yield(yield_supplier);
    }

    // Things that can stay the same (can be templated/annotated)

    // External

    public <T> FutureFor letComp(final AnyVal<T> any, final Supplier<T> let_supplier) {
        _delegate.letComp(any, let_supplier);
        return this;
    }
    public FutureFor filter(final Supplier<Boolean> test) {
        _delegate.filter(test);
        return this;
    }

    // Internal

    private final MonadicFor<CompletableFuture> _delegate;
    private FutureFor(final MonadicFor<CompletableFuture> delegate) {
        _delegate = delegate;
    }
    private static final FutureForWrapper _wrapper = new FutureForWrapper();

}

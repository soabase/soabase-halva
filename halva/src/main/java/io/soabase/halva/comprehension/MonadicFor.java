package io.soabase.halva.comprehension;

import io.soabase.halva.any.AnyVal;

import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.Function;

/** Interface class for the non-type-safe version "for comprehension" for Monadic classes
 *  TODO add documentation for creating new types
 */
public interface MonadicFor<M> {

    /** Defines how a given (monadic) type should implement map/flatMap and optionally filter
     *  The specification isn't type safe so you'll have to be careful for these 2 lines...
     *  (which has meaning in some cases, eg Optional/Stream and not in others, eg CompletableFuture, Validation)
     *
     * @param <MM> - the monad class, ie the same as the `M` in the parent class
     */
    interface MonadicForWrapper<MM> {
        <A> MM flatMap(final MM m, final Function<A, MM> flat_mapper);
        <A, B> MM map(final MM m, final Function<A, B> flatMapper);
        default MM filter(final MM m, Predicate predicate) { throw new RuntimeException("NO FILTER"); }
    }

    /**
     * Constructor - intended to be replaced by the type specific implementation
     * (which will then hide away the `wrapper`)
     * @param any - the variable which inside the for comprehension contains the value of the value(s) inside the `starting_monad`
     * @param starting_monad
     * @param wrapper
     * @param <M> - the type of the monad
     * @param <R> - the type of the variable to which the values inside the monad from this stage are assigned
     * @return
     */
    static <M, R> MonadicFor<M> forComp(final AnyVal<R> any, final M starting_monad, MonadicForWrapper<M> wrapper) {
        return new MonadicForImpl(any, starting_monad, wrapper);
    }
    <R> MonadicFor<M> forComp(final AnyVal<R> any, final Supplier<M> monad_supplier);
    <T> MonadicFor<M> letComp(final AnyVal<T> any, final Supplier<T> let_supplier);
    MonadicFor<M> filter(final Supplier<Boolean> test);
    <R> M yield(Supplier<R> yield_supplier);
}

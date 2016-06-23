package io.soabase.halva.comprehension;

import io.soabase.halva.any.AnyVal;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.Predicate;

/** For comprehension working with Optional
 * Created by Alex on 6/22/2016.
 */
public class OptionFor {

    // Things that need to change

    public static class OptionForWrapper implements MonadicFor.MonadicForWrapper<Optional> {

        public <A> Optional flatMap(final Optional m, final Function<A, Optional> flat_mapper) {
            return m.flatMap(flat_mapper);
        }
        public <A, B> Optional map(final Optional m, final Function<A, B> mapper) {
            return m.map(mapper);
        }
        public <A, B> Optional filter(final Optional m, final Predicate<A> test) {
            return m.filter(test);
        }
    }
    public static <R> OptionFor forComp(final AnyVal<R> any, final Optional<R> starting_monad) {
        return new OptionFor(new MonadicForImpl(any, starting_monad, _wrapper));
    }
    public <R> OptionFor forComp(final AnyVal<R> any, final Supplier<Optional<R>> monad_supplier) {
        _delegate.forComp(any, (Supplier<Optional>)(Supplier<?>)monad_supplier);
        return this;
    }
    public <R> Optional<R> yield(Supplier<R> yield_supplier) {
        return _delegate.yield(yield_supplier);
    }

    // Things that can stay the same (can be templated/annotated)

    // External

    public <T> OptionFor letComp(final AnyVal<T> any, final Supplier<T> let_supplier) {
        _delegate.letComp(any, let_supplier);
        return this;
    }
    public OptionFor filter(final Supplier<Boolean> test) {
        _delegate.filter(test);
        return this;
    }

    // Internal

    protected final MonadicFor<Optional> _delegate;
    protected OptionFor(final MonadicFor<Optional> delegate) {
        _delegate = delegate;
    }
    protected static OptionForWrapper _wrapper = new OptionForWrapper();

}

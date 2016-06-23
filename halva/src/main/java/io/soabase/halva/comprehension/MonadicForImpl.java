package io.soabase.halva.comprehension;

import io.soabase.halva.any.Any;
import io.soabase.halva.any.AnyVal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/** Implementation for inner-type-less MonadifcFor
 * Created by Alex on 6/22/2016.
 */
public class MonadicForImpl<M> implements MonadicFor<M> {

    public <R> MonadicForImpl(final AnyVal<R> any, final M starting_monad, MonadicForWrapper<M> wrapper) {
        _wrapper = wrapper;
        _entries.add(new Entry<>(any, () -> starting_monad, null));
    }

    /////////////////////////////////////////////////////////////

    // OVERRIDES

    @Override
    public <R> MonadicFor<M> forComp(AnyVal<R> any, Supplier<M> monad_supplier) {
        _entries.add(new Entry(any, monad_supplier, null));
        return this;
    }

    @Override
    public <T> MonadicFor<M> letComp(AnyVal<T> any, Supplier<T> let_supplier) {
        _entries.add(new Entry(any, null, let_supplier));
        return this;
    }

    @Override
    public MonadicFor<M> filter(Supplier<Boolean> test) {
        getPreviousEntry().predicates.add(test);
        return this;
    }

    @Override
    public <R> M yield(Supplier<R> yield_supplier) {
        return yieldLoop(0, yield_supplier, null, null);
    }

    /////////////////////////////////////////////////////////////

    // INTERNALS

    final MonadicForWrapper<M> _wrapper;

    // From ForImpl

    private final List<Entry<M>> _entries = new ArrayList<>();

    private static class Entry<M> {
        final Any any;
        final Supplier<M> monad_supplier;
        final Supplier<?> setter;
        final List<Supplier<Boolean>> predicates = new ArrayList<>();

        Entry(Any any, Supplier<M> stream, Supplier<?> setter) {
            this.any = any;
            this.monad_supplier = stream;
            this.setter = setter;
        }
    }

    private Entry<M> getPreviousEntry() {
        if (_entries.size() == 0) {
            throw new IllegalStateException("No generators to apply to");
        }
        return _entries.get(_entries.size() - 1);
    }

    private M yieldLoop(int index, Supplier<?> yielder, Runnable consumer, final M prev_stream) {
        final Entry<M> entry = _entries.get(index);
        M monad = prev_stream;
        if (null != entry.monad_supplier) {
            monad = entry.monad_supplier.get();
            _wrapper.map(monad, o -> { entry.any.set(o); return o; });
        }
        if (null != entry.setter){ // this is a setter
            monad = prev_stream;
            _wrapper.map(monad, o -> { entry.any.set(entry.setter.get()); return o; });
        }

        // Test:
        for (Supplier<Boolean> test : entry.predicates) {
            monad = _wrapper.filter(monad, __ -> test.get());
        }
        // Map
        if ((index + 1) < _entries.size()) {
            final M next_stage_monad = monad;
            monad = _wrapper.flatMap(monad, o -> yieldLoop(index + 1, yielder, consumer, next_stage_monad));
        } else {
            monad = _wrapper.map(monad, o -> {
                if (yielder != null) {
                    return yielder.get();
                }
                if (consumer != null) {
                    consumer.run();
                }
                return o;
            });
        }
        return monad;
    }

}


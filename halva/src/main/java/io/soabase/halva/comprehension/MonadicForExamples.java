package io.soabase.halva.comprehension;

import io.soabase.halva.any.Any;
import io.soabase.halva.any.AnyVal;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by Alex on 6/20/2016.
 */
public class MonadicForExamples {

    public static abstract class MonadicFor<M> {

        ///////////////////////////////////////////////

        // Implement these for every type
        // (Alternative: pass in lambdas/separate-interface - maybe as implicit constructor types)

        protected abstract <A> M flatMap(final M m, final Function<A, M> flat_mapper);
        protected abstract <A, B> M map(final M m, final Function<A, B> flatMapper);
        //TODO - optional filter ... in the annotation case ideally would not provide the .filter call if not present

        // Standard for comprehension
        // Really want M to be M<R> but not possible in java

        public static <M, R> MonadicFor<M> forComp(final AnyVal<R> any, M starting_monad) { return null; }
        public <R> MonadicFor<M> forComp(final AnyVal<R> any, final Supplier<M> monad_supplier) { return null; }
        public <T> MonadicFor<M> letComp(final AnyVal<T> any, final Supplier<T> let_supplier) { return null; }
        public MonadicFor<M> filter(final Supplier<Boolean> predicate) { return null; }
        public <R> M yield(Supplier<R> yield_supplier) { return null; }
    }

    ///////////////////////////////////////////////

    // EXAMPLES - ideally would generate code so that instead could write:

    //@MonadicFor public static class FutureFor extends MonadicFor<CompletableFuture> {
    //and it would create a class with signatures, eg instead of:
    //public <R> MonadicFor<M> forComp(final AnyVal<R> any, final Supplier<M> monad_supplier)
    //public <R> M yield(Supplier<R> yield_supplier)
    //you'd get
    //public <R> MonadicFor<M> forComp(final AnyVal<R> any, final Supplier<M<R> monad_supplier)
    //public <R> M<R> yield(Supplier<R> yield_supplier)


    public static class FutureFor extends MonadicFor<CompletableFuture> {

        protected <A> CompletableFuture flatMap(final CompletableFuture m, final Function<A, CompletableFuture> flat_mapper) {
            return m.thenCompose(flat_mapper);
        }
        protected <A, B> CompletableFuture map(final CompletableFuture m, final Function<A, B> mapper) {
            return m.thenApply(mapper);
        }
    }

    public static class StreamFor extends MonadicFor<Stream> {

        protected <A> Stream flatMap(final Stream m, final Function<A, Stream> flat_mapper) {
            return m.flatMap(flat_mapper);
        }
        protected  <A, B> Stream map(final Stream m, final Function<A, B> mapper) {
            return m.map(mapper);
        }
    }

    public static class OptionalFor extends MonadicFor<Optional> {

        protected <A> Optional flatMap(final Optional m, final Function<A, Optional> flat_mapper) {
            return m.flatMap(flat_mapper);
        }
        protected <A, B> Optional map(final Optional m, final Function<A, B> mapper) {
            return m.map(mapper);
        }
    }

    // Subset of Validation class:
    public static class Validation<E, T> {
        public E fail() { return null; }
        public T success() { return null; }
        public <A> Validation<E,A> bind(final Function<T,Validation<E,A>> f)  { return null; }
        public <A> Validation<E,A> map(final Function<T,A> f) { return null; }
    }

    public static class ValidationFor extends MonadicFor<Validation> {

        protected <A> Validation flatMap(final Validation m, final Function<A, Validation> flat_mapper) {
            return m.bind(flat_mapper);
        }
        protected <A, B> Validation map(final Validation m, final Function<A, B> mapper) {
            return m.map(mapper);
        }
    }

    // (Etc - see also Writer monad, state monad)

    ///////////////////////////////////////////////

    // USAGE EXAMPLES

    public static void main(String[] args) {

        AnyVal<String> a = Any.make();
        AnyVal<String> b = Any.make();
        AnyVal<Integer> c = Any.make();
        AnyVal<Integer> d = Any.make();

        // Issues:
        // 1) annoying cast necessary at initialization
        // 2) unchecked types
        // 3) return type not checked

        final CompletableFuture<Integer> f =
                FutureFor
                        .forComp(a, (CompletableFuture)CompletableFuture.completedFuture("a"))
                        .forComp(b, () -> CompletableFuture.completedFuture(a.val() + "b"))
                        .letComp(c, () -> b.val().length())
                        .forComp(d, () -> CompletableFuture.completedFuture(c.val()))
                        .yield(() -> 1 + d.val())
                ;

        final Optional<String> opt =
                OptionalFor
                        .forComp(a, (Optional)Optional.of("a"))
                        .forComp(c, () -> Optional.empty())
                        .forComp(d, () -> Optional.of(a.val().length() + c.val()))
                        .yield(() -> d.val().toString())
                ;

        //etc
    }

}

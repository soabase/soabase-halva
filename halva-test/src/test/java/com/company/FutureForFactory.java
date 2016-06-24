package com.company;

import io.soabase.halva.comprehension.MonadicFor;
import io.soabase.halva.comprehension.MonadicForWrapper;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@MonadicFor
public class FutureForFactory implements MonadicForWrapper<CompletableFuture>
{
    @SuppressWarnings("unchecked")
    public <A> CompletableFuture flatMap(CompletableFuture m, Function<A, ? extends CompletableFuture> flatMapper)
    {
        return m.thenCompose(flatMapper);
    }

    @SuppressWarnings("unchecked")
    public <A, B> CompletableFuture map(CompletableFuture m, final Function<A, B> mapper)
    {
        return m.thenApply(mapper);
    }
}

package io.soabase.halva.forcomp;

import io.soabase.halva.any.Any;
import io.soabase.halva.any.AnyVal;
import io.soabase.halva.comprehension.FutureFor;
import org.junit.Test;
import org.junit.Assert;


import java.util.concurrent.CompletableFuture;

/**
 * Created by Alex on 6/22/2016.
 */
public class TestFutureFor {

    @Test
    public void test_futureFor() throws Exception {
        final AnyVal<String> a = Any.make();
        final AnyVal<String> b = Any.make();
        final AnyVal<Integer> c = Any.make();
        final AnyVal<Integer> d = Any.make();

        final CompletableFuture<Integer> f =
                FutureFor
                        .forComp(a, CompletableFuture.completedFuture("a"))
                        .forComp(b, () -> CompletableFuture.completedFuture(a.val() + "b"))
                        .letComp(c, () -> b.val().length())
                        .forComp(d, () -> CompletableFuture.completedFuture(c.val()))
                        .yield(() -> 1 + d.val())
                ;
        Assert.assertEquals(3, f.get().intValue());
    }
}

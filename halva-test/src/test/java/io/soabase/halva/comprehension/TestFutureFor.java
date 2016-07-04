/**
 * Copyright 2016 Jordan Zimmerman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.soabase.halva.comprehension;

import com.company.FutureFor;
import io.soabase.halva.any.Any;
import io.soabase.halva.any.AnyVal;
import org.junit.Assert;
import org.junit.Test;
import java.util.concurrent.CompletableFuture;

public class TestFutureFor {

    @Test
    public void testFutureFor() throws Exception {
        final AnyVal<String> a = Any.any();
        final AnyVal<String> b = Any.any();
        final AnyVal<Integer> c = Any.any();
        final AnyVal<Integer> d = Any.any();

        final CompletableFuture<Integer> f =
                FutureFor.start()
                        .forComp(a, () -> CompletableFuture.completedFuture("a"))
                        .forComp(b, () -> CompletableFuture.completedFuture(a.val() + "b"))
                        .letComp(c, () -> b.val().length())
                        .forComp(d, () -> CompletableFuture.completedFuture(c.val()))
                        .yield(() -> 1 + d.val())
                ;
        Assert.assertEquals(3, f.get().intValue());
    }
}

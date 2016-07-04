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

import com.company.OptionalFor;
import io.soabase.halva.any.Any;
import io.soabase.halva.any.AnyVal;
import org.junit.Assert;
import org.junit.Test;
import java.util.Optional;

public class TestOptionFor {

    @Test
    public void testOptionFor() throws Exception
    {
        final AnyVal<String> a = Any.any();
        final AnyVal<Integer> c = Any.any();
        final AnyVal<Integer> d = Any.any();

        final Optional<String> opt =
                OptionalFor.start()
                        .forComp(a, () -> Optional.of("a"))
                        .forComp(c, Optional::empty)
                        .forComp(d, () -> Optional.of(a.val().length() + c.val()))
                        .yield(() -> d.val().toString())
                ;

        Assert.assertEquals(Optional.empty(), opt);
    }
}

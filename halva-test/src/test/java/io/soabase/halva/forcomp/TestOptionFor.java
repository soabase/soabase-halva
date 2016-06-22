package io.soabase.halva.forcomp;

import io.soabase.halva.any.Any;
import io.soabase.halva.any.AnyVal;
import io.soabase.halva.comprehension.OptionFor;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

/**
 * Created by Alex on 6/22/2016.
 */
public class TestOptionFor {

    @Test
    public void test_optionFor() throws Exception {

        final AnyVal<String> a = Any.make();
        final AnyVal<String> b = Any.make();
        final AnyVal<Integer> c = Any.make();
        final AnyVal<Integer> d = Any.make();

        final Optional<String> opt =
                OptionFor
                        .forComp(a, (Optional)Optional.of("a"))
                        .forComp(c, () -> Optional.empty())
                        .forComp(d, () -> Optional.of(a.val().length() + c.val()))
                        .yield(() -> d.val().toString())
                ;

        Assert.assertEquals(Optional.empty(), opt);
    }
}

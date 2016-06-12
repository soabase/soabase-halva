package io.soabase.halva.nettests;

import io.soabase.halva.any.Any;
import io.soabase.halva.caseclass.CaseClass;
import io.soabase.halva.sugar.ConsList;
import org.junit.Assert;
import org.junit.Test;
import java.util.List;
import java.util.stream.Collectors;

import static io.soabase.halva.any.AnyDeclaration.anyInt;
import static io.soabase.halva.any.AnyDeclaration.anyString;
import static io.soabase.halva.matcher.Matcher.match;
import static io.soabase.halva.nettests.PhoneExt.PhoneExt;
import static io.soabase.halva.nettests.PhoneExt.PhoneExtT;
import static io.soabase.halva.sugar.Sugar.List;

public class TestFromScalaSchool
{
    /*
    from https://twitter.github.io/scala_school/pattern-matching-and-functional-composition.html

    "The mystery of case"

  scala> case class PhoneExt(name: String, ext: Int)
  defined class PhoneExt

  scala> val extensions = List(PhoneExt("steve", 100), PhoneExt("robey", 200))
  extensions: List[PhoneExt] = List(PhoneExt(steve,100), PhoneExt(robey,200))

  scala> extensions.filter { case PhoneExt(name, extension) => extension < 200 }
  res0: List[PhoneExt] = List(PhoneExt(steve,100))
     */
    @CaseClass
    interface PhoneExt_
    {
        String name();

        int ext();
    }

    @Test
    public void testTheMysteryCase()
    {
        Any<String> name = anyString.define();
        Any<Integer> extension = anyInt.define();
        ConsList<PhoneExt> extensions = List(PhoneExt("steve", 100), PhoneExt("robey", 200));
        List<PhoneExt> lessThan200 = extensions.stream()
            .filter(e -> match(e).caseOf(PhoneExtT(name, extension), () -> extension.val() < 200).get())
            .collect(Collectors.toList());
        Assert.assertEquals(List(PhoneExt("steve", 100)), lessThan200);
    }
}

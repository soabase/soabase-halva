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
package io.soabase.halva.nettests;

import io.soabase.halva.any.AnyVal;
import io.soabase.halva.caseclass.CaseClass;
import io.soabase.halva.sugar.ConsList;
import org.junit.Assert;
import org.junit.Test;
import java.util.List;
import java.util.stream.Collectors;

import static io.soabase.halva.matcher.Matcher.match;
import static io.soabase.halva.nettests.PhoneExt.PhoneExt;
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
        AnyVal<String> name = new AnyVal<String>(){};
        AnyVal<Integer> extension = new AnyVal<Integer>(){};
        ConsList<PhoneExt> extensions = List(PhoneExt("steve", 100), PhoneExt("robey", 200));
        List<PhoneExt> lessThan200 = extensions.stream()
            .filter(e -> match(e).caseOf(PhoneExt(name, extension), () -> extension.val() < 200).get())
            .collect(Collectors.toList());
        Assert.assertEquals(List(PhoneExt("steve", 100)), lessThan200);
    }
}

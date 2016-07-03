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

import io.soabase.halva.any.Match;
import io.soabase.halva.caseclass.CaseClass;
import io.soabase.halva.any.Any;
import io.soabase.halva.any.AnyType;
import io.soabase.halva.matcher.Matcher;
import io.soabase.halva.matcher.Partial;
import org.junit.Assert;
import org.junit.Test;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.soabase.halva.matcher.Matcher.match;
import static io.soabase.halva.matcher.Matcher.partial;
import static io.soabase.halva.nettests.BinOp.BinOp;
import static io.soabase.halva.nettests.BinOp.BinOpTu;
import static io.soabase.halva.nettests.Number.Number;
import static io.soabase.halva.nettests.UnOp.UnOp;
import static io.soabase.halva.nettests.UnOp.UnOpTu;
import static io.soabase.halva.nettests.Var.Var;

// from http://www.artima.com/pins1ed/case-classes-and-pattern-matching.html
public class TestCaseClassesAndPatternMatching
{
    /*
        abstract class Expr
        case class Var(name: String) extends Expr
        case class Number(num: Double) extends Expr
        case class UnOp(operator: String, arg: Expr) extends Expr
        case class BinOp(operator: String, left: Expr, right: Expr) extends Expr
     */
    interface Expr{}
    @CaseClass interface Var_ extends Expr{String name();}
    @CaseClass interface Number_ extends Expr{double num();}
    @CaseClass interface UnOp_ extends Expr{String operator(); Expr arg();}
    @CaseClass interface BinOp_ extends Expr{String operator(); Expr left(); Expr right();}

    /*
        scala> val v = Var("x")

        scala> val op = BinOp("+", Number(1), v)
    */
    final Var v = Var("x");
    final BinOp op = BinOp("+", Number(1), v);

    /*
        scala> v.name
        res0: String = x

        scala> op.left
        res1: Expr = Number(1.0)

        scala> println(op)
        BinOp(+,Number(1.0),Var(x))

        scala> op.right == Var("x")
        res3: Boolean = true
     */
    @Test
    public void testCaseClassValues()
    {
        Assert.assertEquals("x", v.name());
        Assert.assertEquals("Number(1.0)", op.left().toString());
        Assert.assertEquals("BinOp(\"+\", Number(1.0), Var(\"x\"))", op.toString());
        Assert.assertEquals(op.right(), Var("x"));
    }

    /*
        def simplifyTop(expr: Expr): Expr = expr match {
          case UnOp("-", UnOp("-", e))  => e   // Double negation
          case BinOp("+", e, Number(0)) => e   // Adding zero
          case BinOp("*", e, Number(1)) => e   // Multiplying by one
          case _ => expr
        }

        scala> simplifyTop(UnOp("-", UnOp("-", Var("x"))))
        res4: Expr = Var(x)
     */
    Expr simplifyTop(Expr expr)
    {
        Any<Expr> e = new AnyType<Expr>(){};
        return match(expr)
            .caseOf( UnOpTu("-", UnOpTu("-", e)), e::val)   // Double negation
            .caseOf( BinOpTu("+", e, Number(0)), e::val)   // Adding zero
            .caseOf( BinOpTu("*", e, Number(1)), e::val)   // Multiplying by one
            .caseOf( () -> expr )
            .get()
            ;
    }

    @Test
    public void testSimplifyTop()
    {
        Assert.assertEquals(Var("x"), simplifyTop(UnOp("-", UnOp("-", Var("x")))));
    }

    /*
        def generalSize(x: Any) = x match {
            case s: String => s.length
            case m: Map[_, _] => m.size
            case _ => -1
        }

        scala> generalSize("abc")
        res14: Int = 3

        scala> generalSize(Map(1 -> 'a', 2 -> 'b'))
        res15: Int = 2

        scala> generalSize(Math.Pi)
        res16: Int = -1
     */
    int generalSize(Object x)
    {
        Any<String> anyStr = new AnyType<String>(){};

        Any<Map> anyMap = new AnyType<Map>(){};
        return match(x)
            .caseOf(anyStr, () -> anyStr.val().length())
            .caseOf(anyMap, () -> anyMap.val().size())
            .caseOf(() -> -1)
            .get();
    }

    @Test
    public void testGeneralSize()
    {
        Map<Integer, Character> aMap = new HashMap<>();
        aMap.put(1, 'a');
        aMap.put(2, 'b');
        Assert.assertEquals(3, generalSize("abc"));
        Assert.assertEquals(2, generalSize(aMap));
        Assert.assertEquals(-1, generalSize(Math.PI));
    }

    /*
        val withDefault: Option[Int] => Int = {
            case Some(x) => x
            case None => 0
          }

          scala> withDefault(Some(10))
          res25: Int = 10

          scala> withDefault(None)
          res26: Int = 0
     */
    private static final Match<Optional<Integer>> opt = new Match<Optional<Integer>>(){};
    final Partial<Optional<Integer>> withDefault = Matcher.<Optional<Integer>>partial()
        .caseOf(Any.anyOptional(opt), () -> opt.val().isPresent(), () -> opt.val().get())
        .caseOf(() -> 0);

    @Test
    public void testPartial()
    {
        Assert.assertEquals((Integer)10, withDefault.with(Optional.of(10)).get());
        Assert.assertEquals((Integer)0, withDefault.with(Optional.empty()).get());
    }
}

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
import org.junit.Assert;
import org.junit.Test;
import java.util.function.Predicate;

import static io.soabase.halva.matcher.Matcher.match;
import static io.soabase.halva.nettests.Add.Add;
import static io.soabase.halva.nettests.Add.AddAny;
import static io.soabase.halva.nettests.Const.Const;
import static io.soabase.halva.nettests.Const.ConstAny;
import static io.soabase.halva.nettests.Mult.Mult;
import static io.soabase.halva.nettests.Mult.MultAny;
import static io.soabase.halva.nettests.Neg.Neg;
import static io.soabase.halva.nettests.Neg.NegAny;
import static io.soabase.halva.nettests.X.X;
import static io.soabase.halva.tuple.Tuple.Tu;

// https://kerflyn.wordpress.com/2011/02/14/playing-with-scalas-pattern-matching/
public class TestPlayingWithScalaPatternMatching
{
    /*
        def toYesOrNo(choice: Int): String = choice match {
            case 1 => "yes"
            case 0 => "no"
            case _ => "error"
          }

        So if  you enter toYesOrNo(1), Scala says “yes”. And if you enter toYesOrNo(2), Scala says “error”.
      */

    public String toYesOrNo(int choice)
    {
        return match(choice)
            .caseOf(1, () -> "yes")
            .caseOf(0, () -> "no")
            .caseOf(() -> "error")
            .get();
    }

    @Test
    public void testTraditionalApproach()
    {
        Assert.assertEquals("yes", toYesOrNo(1));
        Assert.assertEquals("error", toYesOrNo(2));
    }

    /*
        But if you want that Scala says “yes” when you enter toYesOrNo(1), toYesOrNo(2), or toYesOrNo(3), you will write the function like this:

        def toYesOrNo(choice: Int): String = choice match {
            case 1 | 2 | 3 => "yes"
            case 0 => "no"
            case _ => "error"
          }
     */
    public String toYesOrNo2(int choice)
    {
        return match(choice)
            .caseOfTest((x) -> (x == 1) || (x == 2) || (x == 3), () -> "yes")
            .caseOf(0, () -> "no")
            .caseOf(() -> "error")
            .get();
    }


    @Test
    public void testTraditionalApproach2()
    {
        Assert.assertEquals("yes", toYesOrNo2(1));
        Assert.assertEquals("yes", toYesOrNo2(2));
        Assert.assertEquals("yes", toYesOrNo2(3));
        Assert.assertEquals("no", toYesOrNo2(0));
        Assert.assertEquals("error", toYesOrNo2(-100));
    }

    /*
        Now, you can use a string for each case entry. Using strings is interesting when you want to parse options of your applications:

        def parseArgument(arg: String) = arg match {
            case "-h" | "--help" => displayHelp
            case "-v" | "--version" => displayVerion
            case whatever => unknownArgument(whatever)
          }

        So if you enter parseArgument(“-h”) or parseArgument(“–help”), Scala calls the displayHelp function. And if you enter
        parseArgument(“huh?”), Scala calls unknownArgument(“huh?”).
     */
    public String parseArgument(String arg)
    {
        AnyVal<String> argAny = new AnyVal<String>(){};
        return match(arg)
            .caseOfTest((s) -> s.equals("-h") || s.equals("--help"), () -> "displayHelp")
            .caseOfTest((s) -> s.equals("-v") || s.equals("--version"), () -> "displayVersion")
            .caseOf(argAny, () -> "unknownArgument(\"" + argAny.val() + "\")")
            .get();
    }

    @Test
    public void testParseArgument()
    {
        Assert.assertEquals("displayHelp", parseArgument("-h"));
        Assert.assertEquals("displayHelp", parseArgument("--help"));
        Assert.assertEquals("unknownArgument(\"huh?\")", parseArgument("huh?"));
    }

    /*
        def f(x: Any): String = x match {
            case i:Int => "integer: " + i
            case _:Double => "a double"
            case s:String => "I want to say " + s
          }
        f(1) → “integer: 1”
        f(1.0) → “a double”
        f(“hello”) → “I want to say hello”
     */
    String f(Object x)
    {
        AnyVal<Integer> i = new AnyVal<Integer>(){};
        AnyVal<Double> d = new AnyVal<Double>(){};
        AnyVal<String> s = new AnyVal<String>(){};
        return match(x)
            .caseOf(i, () -> "integer: " + i.val())
            .caseOf(d, () -> "a double")
            .caseOf(s, () -> "I want to say " + s.val())
            .get();
    }

    @Test
    public void testTypedPattern()
    {
        Assert.assertEquals("integer: 1", f(1));
        Assert.assertEquals("a double", f(1.0));
        Assert.assertEquals("I want to say hello", f("hello"));
    }

    /*
        def fact(n: Int): Int = n match {
            case 0 => 1
            case n => n * fact(n - 1)
          }
     */
    int fact(int n)
    {
        AnyVal<Integer> i = new AnyVal<Integer>(){};
        return match(n)
            .caseOf(0, () -> 1)
            .caseOf(i, () -> {
                int v = i.val();
                return v * fact(v - 1);
            })
            .get();
    }

    @Test
    public void testFact()
    {
        Assert.assertEquals(1, fact(0));
        Assert.assertEquals(1, fact(1));
        Assert.assertEquals(2, fact(2));
        Assert.assertEquals(5040, fact(7));
    }

    /*
        def parseArgument(arg : String, value: Any) = (arg, value) match {
          case ("-l", lang) => setLanguageTo(lang)
          case ("-o" | "--optim", n : Int) if ((0 < n) && (n <= 5)) => setOptimizationLevelTo(n)
          case ("-o" | "--optim", badLevel) => badOptimizationLevel(badLevel)
          case ("-h" | "--help", null) => displayHelp()
          case bad => badArgument(bad)
        }
    */
    String parseArgument2(String arg, Object value)
    {
        Predicate<String> oOrOptim = s -> s.equals("-o") || s.equals("--optim");
        Predicate<String> hOrHelp = s -> s.equals("-h") || s.equals("--help");

        AnyVal<Integer> n = new AnyVal<Integer>(){};
        AnyVal<String> s = new AnyVal<String>(){};
        AnyVal<String> v = new AnyVal<String>(){};
        return match(Tu(arg, value))
            .caseOf(Tu("-l", s), () -> "setLanguageTo(" + s.val() + ")")
            .caseOf(Tu(oOrOptim, n), () -> ((0 < n.val()) && (n.val() <= 5)), () -> "setOptimizationLevelTo(" + n.val() + ")")
            .caseOf(Tu(oOrOptim, n), () -> "badOptimizationLevel(" + n.val() + ")")
            .caseOf(Tu(hOrHelp, v), () -> "displayHelp()")
            .caseOf(Tu(s, v), () -> "badArgument(" + s.val() + ")")
            .get();
    }

    @Test
    public void testParse2()
    {
        Assert.assertEquals("setLanguageTo(english)", parseArgument2("-l", "english"));
        Assert.assertEquals("setOptimizationLevelTo(3)", parseArgument2("--optim", 3));
        Assert.assertEquals("badOptimizationLevel(-100)", parseArgument2("--optim", -100));
        Assert.assertEquals("displayHelp()", parseArgument2("-h", ""));
        Assert.assertEquals("badArgument(--foo)", parseArgument2("--foo", "bar"));
    }

    /*
        sealed abstract class Expression
        case class X() extends Expression
        case class Const(value : Int) extends Expression
        case class Add(left : Expression, right : Expression) extends Expression
        case class Mult(left : Expression, right : Expression) extends Expression
        case class Neg(expr : Expression) extends Expression
        Now, lets define a function to evaluate expressions with a given value for the variable by using pattern matching.

        def eval(expression : Expression, xValue : Int) : Int = expression match {
          case X() => xValue
          case Const(cst) => cst
          case Add(left, right) => eval(left, xValue) + eval(right, xValue)
          case Mult(left, right) => eval(left, xValue) * eval(right, xValue)
          case Neg(expr) => - eval(expr, xValue)
        }
        Lets try the eval() function:

        val expr = Add(Const(1), Mult(Const(2), Mult(X(), X())))  // 1 + 2 * X*X
        assert(eval(expr, 3) == 19)
        Now, we define a function that compute the (unreduced) derivative of an expression:

        def deriv(expression : Expression) : Expression = expression match {
          case X() => Const(1)
          case Const(_) => Const(0)
          case Add(left, right) => Add(deriv(left), deriv(right))
          case Mult(left, right) => Add(Mult(deriv(left), right), Mult(left, deriv(right)))
          case Neg(expr) => Neg(deriv(expr))
        }
        Lets try the deriv() function:

        val df = deriv(expr)
        Here is what you get in df:

        Add(Const(0),Add(Mult(Const(0),Mult(X(),X())),Mult(Const(2),Add(Mult(Const(1),X()),Mult(X(),Const(1))))))
        // = 0 + (0 * X*X + 2 * (1*X + X*1)) = 4 * X
        assert(eval(df, 3), 12)
    */
    interface Expression{}
    @CaseClass
    interface X_ extends Expression{}
    @CaseClass interface Const_ extends Expression{int value();}
    @CaseClass interface Add_ extends Expression{Expression left(); Expression right();}
    @CaseClass interface Mult_ extends Expression{Expression left(); Expression right();}
    @CaseClass interface Neg_ extends Expression{Expression expr();}

    /*
        def eval(expression : Expression, xValue : Int) : Int = expression match {
          case X() => xValue
          case Const(cst) => cst
          case Add(left, right) => eval(left, xValue) + eval(right, xValue)
          case Mult(left, right) => eval(left, xValue) * eval(right, xValue)
          case Neg(expr) => - eval(expr, xValue)
        }
     */
    int eval(Expression expression, int xValue)
    {
        AnyVal<Integer> cst = new AnyVal<Integer>(){};
        AnyVal<Expression> addLeft = new AnyVal<Expression>(){};
        AnyVal<Expression> addRight = new AnyVal<Expression>(){};
        AnyVal<Expression> multLeft = new AnyVal<Expression>(){};
        AnyVal<Expression> multRight = new AnyVal<Expression>(){};
        AnyVal<Expression> expr = new AnyVal<Expression>(){};

        return match(expression)
            .caseOf(X(), () -> xValue)
            .caseOf(ConstAny(cst), cst::val)
            .caseOf(AddAny(addLeft, addRight), () -> eval(addLeft.val(), xValue) + eval(addRight.val(), xValue))
            .caseOf(MultAny(multLeft, multRight), () -> eval(multLeft.val(), xValue) * eval(multRight.val(), xValue))
            .caseOf(NegAny(expr), () -> -1 * eval(expr.val(), xValue))
            .get();
    }

    /*
        def deriv(expression : Expression) : Expression = expression match {
          case X() => Const(1)
          case Const(_) => Const(0)
          case Add(left, right) => Add(deriv(left), deriv(right))
          case Mult(left, right) => Add(Mult(deriv(left), right), Mult(left, deriv(right)))
          case Neg(expr) => Neg(deriv(expr))
        }
     */
    Expression deriv(Expression expression)
    {
        AnyVal<Integer> cst = new AnyVal<Integer>(){};
        AnyVal<Expression> addLeft = new AnyVal<Expression>(){};
        AnyVal<Expression> addRight = new AnyVal<Expression>(){};
        AnyVal<Expression> multLeft = new AnyVal<Expression>(){};
        AnyVal<Expression> multRight = new AnyVal<Expression>(){};
        AnyVal<Expression> expr = new AnyVal<Expression>(){};

        return match(expression)
            .caseOf(X(), () -> (Expression)Const(1))
            .caseOf(ConstAny(cst), () -> Const(0))
            .caseOf(AddAny(addLeft, addRight), () -> Add(deriv(addLeft.val()), deriv(addRight.val())))
            .caseOf(MultAny(multLeft, multRight), () -> Add(Mult(deriv(multLeft.val()), multRight.val()), Mult(multLeft.val(), deriv(multRight.val()))))
            .caseOf(NegAny(expr), () -> Neg(deriv(expr.val())))
            .get();
    }

    @Test
    public void testEvalAndDeriv()
    {
        // Add(Const(1), Mult(Const(2), Mult(X(), X())))  // 1 + 2 * X*X
        Expression expr = Add(Const(1), Mult(Const(2), Mult(X(), X())));
        Assert.assertEquals(19, eval(expr, 3));

        Expression df = deriv(expr);
        Assert.assertEquals(12, eval(df, 3));
    }
}

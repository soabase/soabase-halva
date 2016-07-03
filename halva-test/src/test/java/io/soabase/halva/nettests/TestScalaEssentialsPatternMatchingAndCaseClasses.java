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

import io.soabase.halva.any.Any;
import io.soabase.halva.any.AnyType;
import io.soabase.halva.caseclass.CaseClass;
import io.soabase.halva.matcher.Match;
import io.soabase.halva.matcher.Partial;
import io.soabase.halva.tuple.ClassTuple;
import io.soabase.halva.tuple.Tuplable;
import io.soabase.halva.tuple.Tuple;
import org.junit.Assert;
import org.junit.Test;
import java.util.List;
import java.util.Optional;

import static io.soabase.halva.matcher.Match.any;
import static io.soabase.halva.matcher.Match.val;
import static io.soabase.halva.matcher.Matcher.match;
import static io.soabase.halva.matcher.Matcher.partial;
import static io.soabase.halva.nettests.Cash.Cash;
import static io.soabase.halva.nettests.Civilian.Civilian;
import static io.soabase.halva.nettests.Guy.Guy;
import static io.soabase.halva.nettests.Guy.GuyTu;
import static io.soabase.halva.nettests.SuperHero.SuperHero;
import static io.soabase.halva.nettests.SuperHero.SuperHeroTu;
import static io.soabase.halva.nettests.TestScalaEssentialsPatternMatchingAndCaseClasses.Power.*;
import static io.soabase.halva.nettests.Villain.Villain;
import static io.soabase.halva.sugar.Sugar.List;

// from http://www.slideshare.net/czechscala/scala-essentials-pattern-matching-and-case-classes
// and https://gist.github.com/ksmutny/5439360
public class TestScalaEssentialsPatternMatchingAndCaseClasses
{
    String whatIsThis(int i)
    {
        return match(i)
            .caseOfTest(x -> x == 8 || x == 10, () -> "something")
            .caseOf(12, () -> "somethingElse")
            .caseOf(() -> "default")
            .get();
    }

    @Test
    public void testSwitchReplacement()
    {
        Assert.assertEquals("something", whatIsThis(8));
        Assert.assertEquals("something", whatIsThis(10));
        Assert.assertEquals("somethingElse", whatIsThis(12));
        Assert.assertEquals("default", whatIsThis(100));
        Assert.assertEquals("default", whatIsThis(-1022));
    }

    // https://gist.github.com/ksmutny/5439360

    enum Power
    {SuperhumanStrength, Genius, Cyborg, Gadgets, Invulnerability}
    interface Wealth{default int n(){return 0;}}   // I had to add n here - I don't understand how it works without it
    // case class Cash(n: Int) extends Wealth
    @CaseClass interface Cash_ extends Wealth{int n();}
    // case object Fortune extends Wealth
    static Wealth Fortune = new Wealth(){
        @Override
        public int n()
        {
            return Integer.MAX_VALUE;
        }
    };

    /*
        sealed trait Character {
            def name: String
        }
    */
    interface Character
    {
        String name();

        static ClassTuple CharacterT(Object _1, Object _2) {
            return () -> Tuple.Tu(_1, _2);
        }
    }

    // case class Civilian(name: String, wealth: Wealth) extends Character
    // case class SuperHero(name: String, powers: List[Power], alterEgo: Option[Civilian]) extends Character
    // case class Villain(name: String, archEnemy: SuperHero) extends Character
    @CaseClass interface Civilian_ extends Character{String name(); Wealth wealth();}
    @CaseClass interface SuperHero_ extends Character{String name(); List<Power> powers(); Optional<Civilian> alterEgo();}
    @CaseClass interface Villain_ extends Character{String name(); SuperHero archEnemy();}

    /*
        trait Person {
            def name: String
            def age: Int
          }

        object Person {
            def unapply(person: Person): Option[(String, Int)] = Some((person.name, person.age))
          }

        case class Guy(name: String, age: Int) extends Person
     */
    interface Person extends Tuplable
    {
        String name();
        int age();

        static ClassTuple PersonT(Object _1, Object _2) {
            return () -> Tuple.Tu(_1, _2);
        }

        default Tuple tuple()
        {
            return Tuple.Tu(name(), age());
        }
    }
    @CaseClass interface Guy_ extends Person{String name(); int age();}

    /*
      val TonyStark = Civilian("Tony Stark", Fortune)
      val BruceWayne = Civilian("Bruce Wayne", Fortune)
      val ClarkKent = Civilian("Clark Kent", Cash(1000))

      val IronMan = SuperHero("Iron Man", List(SuperhumanStrength, Genius, Cyborg), Some(TonyStark))
      val Batman = SuperHero("Batman", List(Genius, Gadgets), Some(BruceWayne))
      val Superman = SuperHero("SuperMan", List(SuperhumanStrength, Invulnerability), Some(ClarkKent))
     */
    final Civilian TonyStark = Civilian("Tony Stark", Fortune);
    final Civilian BruceWayne = Civilian("Bruce Wayne", Fortune);
    final Civilian ClarkKent = Civilian("Clark Kent", Cash(1000));
    final Civilian MaryJane = Civilian("Mary Jane", Cash(1));

    final SuperHero IronMan = SuperHero("Iron Man", List(SuperhumanStrength, Genius, Cyborg), Optional.of(TonyStark));
    final SuperHero Batman = SuperHero("Batman", List(Genius, Gadgets), Optional.of(BruceWayne));
    final SuperHero Superman = SuperHero("SuperMan", List(SuperhumanStrength, Invulnerability), Optional.of(ClarkKent));

    final Villain Mandarin = Villain("Mandarin", IronMan);
    final Villain Joker = Villain("Joker", Batman);

    @Test
    public void testDrWho()
    {
        /*
            Guy("Dr. Who", Int.MaxValue) match {
                case Person("Dr. Who", _) => "Exactly"
                case ironMan => "anyone!"
              }
         */
        Any<Integer> anyAge = new AnyType<Integer>(){};
        String result = match(Guy("Dr. Who", Integer.MAX_VALUE))
            .caseOf(GuyTu("Dr. Who", anyAge), () -> "Exactly!")
            .caseOf(() -> "anyone!")
            .get();
        Assert.assertEquals("Exactly!", result);
    }

    Optional<List<Power>> getPowers(Character person)
    {
        Match<String> anyName = Match.any(new AnyType<String>(){});
        Match<List<Power>> anyPowers = Match.any(new AnyType<List<Power>>(){});
        return match(person)
            .caseOf(SuperHero(anyName, anyPowers, val(Optional.of(TonyStark))), () -> Optional.of(anyPowers.val()))
            .caseOf(Optional::empty)
            .get();
    }

    // slide 8

    @Test
    public void testUnknownPerson()
    {
        Assert.assertEquals(Optional.of(IronMan.powers()), getPowers(IronMan));
        Assert.assertEquals(Optional.empty(), getPowers(Batman));
    }

    // slide 13/14/17

    String whatIsThis2(Object obj)
    {
        return match(obj)
            .caseOf(42, () -> "a magic no.")
            .caseOf("Hello!", () -> "a greeting")
            .caseOf(Math.PI, () -> "another magic no.")
            .caseOf(() -> "something else")
            .get();
    }

    Optional<String> whatIsThis3(Object obj)
    {
        return match(obj)
            .caseOf(42, () -> "a magic no.")
            .caseOf("Hello!", () -> "a greeting")
            .caseOf(Math.PI, () -> "another magic no.")
            .getOpt();
    }

    String whatIsThis4(Object obj)
    {
        Any<Integer> n = new AnyType<Integer>(){};
        Any<java.lang.Character> c = new AnyType<java.lang.Character>(){};
        return match(obj)
            .caseOf(n, () -> "aah, a number")
            .caseOf(c, () -> "it's " + c.val())
            .get();
    }

    @Test
    public void testWhatIsThis()
    {
        Assert.assertEquals("a magic no.", whatIsThis2(42));
        Assert.assertEquals("a greeting", whatIsThis2("Hello!"));
        Assert.assertEquals("another magic no.", whatIsThis2(Math.PI));
        Assert.assertEquals("something else", whatIsThis2(Math.class));

        Assert.assertEquals(Optional.empty(), whatIsThis3(BruceWayne));

        Assert.assertEquals("aah, a number", whatIsThis4(10));
        Assert.assertEquals("aah, a number", whatIsThis4(20));
        Assert.assertEquals("aah, a number", whatIsThis4(-30));
        Assert.assertEquals("it's X", whatIsThis4('X'));
    }

    // slide 25

    String fromTuple(Tuple aPair)
    {
        Any<Villain> anyVillain = new AnyType<Villain>(){};
        Any<Object> any = new AnyType<Object>(){};
        return match(aPair)
            .caseOf(Tuple.Tu(42, Math.PI, any), () -> "magic numbers + anything")
            .caseOf(Tuple.Tu(IronMan, Mandarin), () -> "hate each other")
            .caseOf(Tuple.Tu(anyVillain, MaryJane), () -> "Cheating with " + anyVillain.val().name())
            .caseOf(() -> "")
            .get();
    }

    @Test
    public void testFromTuple()
    {
        Assert.assertEquals("hate each other", fromTuple(Tuple.Tu(IronMan, Mandarin)));
        Assert.assertEquals("Cheating with Mandarin", fromTuple(Tuple.Tu(Mandarin, MaryJane)));
        Assert.assertEquals("Cheating with Joker", fromTuple(Tuple.Tu(Joker, MaryJane)));
        Assert.assertEquals("magic numbers + anything", fromTuple(Tuple.Tu(42, Math.PI, Joker)));
        Assert.assertEquals("magic numbers + anything", fromTuple(Tuple.Tu(42, Math.PI, String.class)));
    }

    // slide 27

    String getStatus(Character person)
    {
        Any<String> anyName = new AnyType<String>(){};
        Any<Wealth> anyWealth = new AnyType<Wealth>(){};
        return match(person)
            .caseOf(Tuple.Tu(anyName, anyWealth), () -> anyWealth.val().n() >= 10000, () -> "Rich guy")
            .caseOf(() -> "anybody else")
            .get();
    }

    @Test
    public void testPatternGuard()
    {
        Assert.assertEquals("Rich guy", getStatus(TonyStark));
        Assert.assertEquals("Rich guy", getStatus(BruceWayne));
        Assert.assertEquals("anybody else", getStatus(MaryJane));
        Assert.assertEquals("anybody else", getStatus(ClarkKent));
    }

    // slide 31

    @Test
    public void testPartials()
    {
        Any<Integer> anyInt = new AnyType<Integer>(){};
        Partial<Integer> partial = partial(Integer.class)
            .caseOf(8, () -> "eight")
            .caseOf(anyInt, () -> "Number " + anyInt.val());
        Assert.assertEquals("eight", partial.with(8).get());
        Assert.assertEquals("Number 10", partial.with(10).get());
        Assert.assertEquals("Number -246", partial.with(-246).get());
    }
}

### Pattern Matching and Extraction

----

**IMPORTANT** - First read about how Halva extracts values using [Anys](../any/README.md)

----

NOTE: If you are not familiar with Scala's pattern matching you should review [Scala for Java Programmers](../../../../../../../../SCALA.md) first.

------------

Halva adds support to Java for Scala's match/case feature with extraction. The syntax is as close as possible to Scala. The form is:

```
match(arg)
    .caseOf(possibleMatch, () -> expression-when-matching)
    .caseOf(anotherPossibleMatch, () -> expression-when-matching)
 ...    
    .get() // gets the result
```

The full list of methods that `match` supports is:

* `caseOf` - multiple versions
    * `M caseOf(Object lhs, Supplier<T> proc)` - if the object matches the match arg, proc is executed to generate the result of the match
    * `M caseOf(Object lhs, Guard guard, Supplier<T> proc)` - if the object matches the match arg and the guard proc returns true, proc is executed to generate the result of the match
    * `M caseOfUnit(Object lhs, Runnable proc)` - if the object matches arg, proc is executed and `null` is returned
    * `M caseOfUnit(Object lhs, Guard guard, Runnable proc)` - if the object matches arg and the guard proc returns true, proc is executed and `null` is returned
* `caseOf(Supplier<T> proc)` - the default handler - the proc is called if all other cases don't match. This is equivalent to Scala's `case _`
* `get()` - process the match statements and return the matching result or `null`
* `getOpt()` - process the match statements and return an `Optional` that is either empty or the matching result
* `apply()` - process the match statements without returning anything. Useful with the `caseOfUnit()` methods

### Extraction

As the matcher executes, [Any](../any/README.md) variables get loaded with extracted values so that the proceeding lambdas can access them.
    
E.g.

```java
AnyVal<String> str = new AnyVal<String>(){};

match(anotherString)
    .caseOf(str, () -> "It's " + str.val())
    .get();
```

**Extraction and Case Classes**

Generated [Case Classes](../caseclass/README.md) include a special method named *CaseClassName*Any that is used to match/extract values. 
For example, given this Case Class...

```java
@CaseClass interface Person{String name(); int age();}
```

... Halva will generate a Case Class named `PersonCase`. The special extraction method will have the signature:

```java
public static AnyClassTuple<ExampleCase> ExampleCaseAny(AnyVal<? extends String> name, AnyVal<? extends Integer> age)
```

This can be used to match/extract values in the case class. For example,
to match a PersonCase instance with the name "John Galt" and extract the age:

```java
Any<Integer> age = new Any<Integer>(){};
int foundAge = match(person)
    .caseOf(PersonCaseAny(Any.lit("John Galt"), age), () -> age.val())
    .caseOf(() -> 0)
    .get()
```

**Function List Matching/Extraction**

Halva supports some of Scala's list pattern matching. In Scala you can do:

```scala
list match {
    case Pair(x, y) :: tail => ...
}
```

Halva supports this via AnyVals. Given existing Anys you can create a container AnyVal that matches parts of a Halva `ConsList`. E.g.

```java
AnyVal<String> s = new AnyVal<String>(){};
AnyVal<List<Object>> e = new AnyVal<List<Object>>(){};
AnyVal<Object> patternMatcher = Any.anyHeadAnyTail(s, e);
String str = match(list)
    .caseOf(patternMatcher, () -> "The tail is: " + e.val())
    .get();
```

### Partials

The front portion of a matcher can be saved for later use as a Partial. E.g.

```
Any<Integer> anyInt = new AnyType<Integer>(){};
Partial<Integer, Integer> partial = Matcher.<Integer>partial()
    .caseOf(8, () -> "eight")
    .caseOf(anyInt, () -> "Number " + anyInt.val());
    
... later on ...

assertEquals("eight", partial.with(8).get());
assertEquals("Number 10", partial.with(10).get());
assertEquals("Number -246", partial.with(-246).get());
```

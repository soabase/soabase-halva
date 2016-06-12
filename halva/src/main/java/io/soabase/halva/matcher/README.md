### Pattern Matching and Extraction

----

**IMPORTANT** - First read about how Halva extracts values using [Anys](../any/)

----

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
    * `<T> M caseOf(Object lhs, Supplier<T> proc)` - if the object matches the match arg, proc is executed to generate the result of the match
    * `<T> M caseOf(Object lhs, Guard guard, Supplier<T> proc)` - if the object matches the match arg and the guard proc returns true, proc is executed to generate the result of the match
    * `<T> M caseOfUnit(Object lhs, Runnable proc)` - if the object matches arg, proc is executed and `null` is returned
    * `<T> M caseOfUnit(Object lhs, Guard guard, Runnable proc)` - if the object matches arg and the guard proc returns true, proc is executed and `null` is returned
* `caseOf(Supplier<T> proc)` - the default handler - the proc is called if all other cases don't match. This is equivalent to Scala's `case _`
* `get()` - process the match statements and return the matching result or `null`
* `getOpt()` - process the match statements and return an `Optional` that is either empty or the matching result
* `apply()` - process the match statements without returning anything. Useful with the `caseOfUnit()` methods

### Extraction

As the matcher executes, [Any](../any/) variables get loaded with exctracted values so that the proceeding lambdas can access them.
    
E.g.

```java
Any<String> str = AnyDeclaration.anyStr.define();

match(anotherString)
    .caseOf(str, () -> "It's " + str.val())
    .get();
```

**Extraction and Case Classes**

In combination with Case Classes, Scala allows for extremely rich and complicated pattern matching. Halva attempts to support most of what is commonly used. Halva [Case Classes](../caseclass/) adds nuermous methods/features to support extraction. In Scala, you can construct case class instances that have extraction variables as arguments. This is not possible in Java, but we can get very close using Halva. Halva adds a static method to every case class that is the name of the case class suffixed with "T". E.g. if your case class is named "MyCase", the method is named "MyCaseT". This method has the same number of arguments as there are fields in the Case Class. However, the argument type is `Object` so that it can accept any value. Thus, you can pass an `Any` value in any argument position (or multiple positions). The Halva matcher is aware of this syntax and does the appropriate matching and extraction when it is encountered.

E.g.

```java
@CaseClass public interface Animal{String name(); int age();}
@CaseClass public interface Chair{int legQty(); int age();}

public int findAnyAge(Object obj)
{
    Any<String> s = anyString.define();
    Any<Integer> age = anyInt.define();
    return match(obj)
        .caseOf(AnimalCaseT(s, age), age::val)
        .caseOf(ChairCaseT(3, age), age::val)
        .caseOf(() -> 0)
        .get();
}

findAnyAge(AnimalCase("Bobby", 14)) -- 14
findAnyAge(ChairCase(2, 5)) -- 0 - not enough legs
findAnyAge(ChairCase(3, 5)) -- 5
```

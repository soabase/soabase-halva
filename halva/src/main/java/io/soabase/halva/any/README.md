### Anys

In Scala for-comprehensions, pattern matching, etc. you can declare variables that can be used with extraction as the 
comprehension or matching is occurring. Scala will define these variables for you using the correct type and create a new scope for using them. E.g.

```scala
x match {
    case Pair(x, y) => println(s"It's got an $x and a $y")
}
```

This simply isn't possible in Java without some very magical bytecode writing of some kind. Halva is committed to using "plain old" 
Java so a different solution had to be implemented. The Any package is the solution for 
Halva's [For Comprehensions](../comprehension/README.md) and [Matching and Extracting](../matcher/README.md). 
Additionally, Halva [Tuples](../tuple/README.md) recognize the presence of AnyVals for extraction. AnyVals have a `val()` method that 
unboxes/returns the extracted value.

#### Types

AnyVals are used in a number of ways depending on context:
 
 * Simple value box: For comprehension methods use AnyVals to hold the binding value from a sequence being 
 iterated over. AnyVals used in this context are created by the method: `Any.any()`
 * Literal value matches: in pattern matching you may want to match a literal value. There are several methods in `Any` to create literal AnyVals: 
   * `Any.lit()` - creates a match for a constant/literal
   * `Any.anyNull()` - matches any null value
   * `Any.anySome()` - matches any Optional that is loaded with a value. The value is extracted to the given AnyVal.
   * `Any.anyNone()` - matches any empty Optional
   * `Any.anyOptional()` - matches any Optional that is loaded or empty. The value is extracted to the given AnyVal if present.
 * Type-safe extractors: these are used to extract values during pattern matching/extraction.
 * TypeAlias extractors: special-purpose AnyVals that allow matching on generated halva TypeAlias instances. Create via `Any.typeAlias()`
 * List slice extractors: `AnyVals` that match literal/any of the head/tail or any combination from a list.
 * Case Class unapply-style extraction: all generated case classes have a method named *CaseClassName*Any. This method has the same arguments as the Case Class however each argument is wrapped in an AnyVal. The object returned by this method can be passed to Matcher which will apply each argument as appropriate. Use a combination of AnyVal literals or extractors as needed.

#### Using an AnyVal For Extraction

An instance of `AnyVal` is a placeholder for any value of the matching type. During processing, an `AnyVal` instance will compare true for 
equality to any instance of the enclosed type and the value of that instance will be captured by the `AnyVal` so that it can used later on. Because 
of [Type Erasure](https://docs.oracle.com/javase/tutorial/java/generics/genMethods.html) you must use the "type token" idiom to partially-reify type 
information. Thus, you declare/define an `AnyVal` using this pattern:

```java
AnyVal<MyType> anyMyType = new AnyVal<MyType>(){}; // the two braces {} are required
```

Once you have an Any you can use it in pattern matching. E.g.

```java
Any<Integer> i = new AnyType<Integer>(){};
match(aVariable)
    .caseOf(i, "The value is: " + i.val())
    .get();
```

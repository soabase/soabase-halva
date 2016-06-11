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

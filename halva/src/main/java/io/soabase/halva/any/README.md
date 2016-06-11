### Anys

In Scala for-comprehensions, pattern matching, etc. you can declare variables that can be used with extraction as the comprehension or matching is occurring. Scala will define these variables for you using the correct type and create a new scope for using them. E.g.

```scala
x match {
    case Pair(x, y) => println("It's got an " + x + " and a " + y ")
}
```

This simply isn't possible in Java without some very magical bytecode writing of some kind. Halva is committed to using "plain old" Java so a different solution had to be implemented. The Any package is the solution for Halva's [For Comprehensions](../comprehension/) and [Matching and Extracting](../matcher/). Additionally, Halva [Tuples](../tuple) recognize the presence of Anys for extraction. 

An instance an `Any` is a placeholder for any value of the matching type. During processing, an `Any` instance will compare true for equality to any instance of the enclosed type and the value of that instance will be captured by the `Any` so that it can used later on.

#### Declaration vs Definition 

Like any variable in computer science, Anys must first be declared and then defined when they are used. The declaration can be done once and stored in static/constant and reused as needed. Definitions are used as needed and are single use containers/boxes for the declared type of the Any. An any declaration is create as follows:

```java
AnyDeclaration<List<String>> anyListOfString = AnyDeclaration.of(new AnyType<List<String>>(){});
AnyDeclaration<SimpleType.class> anySimple = AnyDeclaration.of(SimpleType.class);
```

The `AnyDeclaration` class pre-declares common Anys such as `anyInt()`, `anyString()`, etc.

#### Usage a Defined Any

Once you have a declaration, you can define and use it in for-comprehensions and matching. E.g.

```java
AnyDeclaration<Pair<String, Integer>> myDecl = AnyDeclaration.of(new AnyType<Pair<String, Integer>>(){});

...

List<Pair<String, Integer>> findMatches(String key, ConsList<Pair<String, Integer>> list) {
    Any<Pair<String, Integer>> foundPair = myDecl.define();

    return For(foundPair, list)
        .when(() -> foundPair.val()._1.equals(key))
        .yield(foundPair::val);
}
```

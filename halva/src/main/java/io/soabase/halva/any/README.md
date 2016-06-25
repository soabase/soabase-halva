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
Additionally, Halva [Tuples](../tuple/README.md) recognize the presence of Anys for extraction. 

#### AnyVal vs Any

Halva has two types of "anys", `AnyVal` and `Any`. `AnyVal` is a simple 
[Boxing](https://en.wikipedia.org/wiki/Object_type_(object-oriented_programming)#Boxing) mechanism 
that is used with Halva's [For Comprehensions](../comprehension/README.md). `Any` is a boxing and 
matching mechanism that is used with Halva's [Matching and Extracting](../matcher/README.md).

#### Using an Any

An instance of `Any` is a placeholder for any value of the matching type. During processing, an `Any` instance will compare true for 
equality to any instance of the enclosed type and the value of that instance will be captured by the `Any` so that it can used later on. Because of [Type Erasure](https://docs.oracle.com/javase/tutorial/java/generics/genMethods.html) you must use the "type token" idiom to partially-reify type information. Thus, you declare/define an `Any` using this pattern:

```java
Any<MyType> anyMyType = new AnyType<MyType>(){}; // the two braces {} are required
```

Once you have an Any you can use it in pattern matching. E.g.

```java
Any<Integer> i = new AnyType<Integer>(){};
match(aVariable)
    .caseOf(i, "The value is: " + i.val())
    .get();
```

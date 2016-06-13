### Scala TL/DR for Java Programmers

* Case Classes and Case Objects
  * Case Classes
  * Difference between Case Object and Case Classes
* Pattern matching and extraction
* For-comprehensions

#### Case Class and Case Objects

##### Case Classes

From http://docs.scala-lang.org/tutorials/tour/case-classes.html -

> Case classes are regular classes which export their constructor parameters and which provide a recursive decomposition mechanism via pattern matching.

A simpler explanation: a Case Class is a POJO generated from a specification of the fields desired. The generated class will have reasonable toString(), hashCode(), equals(), accessor methods, builders, etc. It also has methods to "deconstruct" instances for use in pattern matching.

##### Case Objects

A Case Object is a specialization of a Case Class. It is a singleton version of a Case Class. Because it is a singleton, it has no fields. It is generated from a similar specification to a Case Class but incldues a singleton definition as well.


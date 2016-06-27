### Type Containers

The Halva Annotation Processor generates classes for annotated case classes, etc. When these classes are generated,
they are [Top Level Classes](http://www.cs.mun.ca/~michael/java/jdk1.1.5-docs/guide/innerclasses/spec/innerclasses.doc1.html).
However, it's sometimes convenient to nest the generated classes in a parent class so that you can control access, etc.
Halva supports this with the `@TypeContainer` annotation. 

To generate a type container, declare an interface that contains the other classes you want to generate and the Halva processor 
generates the class during normal javac compilation (you may need to [enable Java Annotation processing](../../../../../../../../IDEs.md) in your IDE/build tool).

#### Example

A type container example is here: [ExampleContainer_.java](https://github.com/Randgalt/halva/blob/master/examples/example-generated/ExampleContainer_.java).
This defines a type container that contains a type alias and a case class.

The generated container is here: [ExampleContainer.java](https://github.com/Randgalt/halva/blob/master/examples/example-generated/ExampleContainer.java). You
then use the alias and case class like this:

```java
ExampleContainer.Stack stack = Stack(List(List("one", "two", "three"), List("four", "five")));
ExampleContainer.MyStack myStack = ExampleContainer.MyStack.MyStack(stack, 10);
```

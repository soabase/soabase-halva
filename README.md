[![Build Status](https://travis-ci.org/soabase/soabase.svg?branch=master)](https://travis-ci.org/soabase/soabase-caseclass)

# CaseClass
Java 8 annotation processor that generates Scala-style case classes.

#### Quick Start

Turn this:

```java
@CaseClass
public interface Example {
    String firstName();
    String lastName();
    int age();
    boolean active();
    List<Date> importantDates();
}
```

... into a complete **Scala-style case class** with **accessors**, a complete **Builder/Factory**, **default values**, **`equals()`**, **`hashCode()`**, **`toString()`** and **`copy()`**. The generated class for the above is here:

> https://github.com/soabase/soabase-caseclass/blob/master/examples/ExampleCase.java

Then you can use it ala:

```java
ExampleCase myExample = ExampleCase.builder().firstName("John").lastName("Galt").age(42).build();
```

Make a copy with field changes ala:

```java
ExampleCase copy = myExample.copy().active(true).build();
```
#### Default Values

Where possible, CaseClass fields are given a default value. However, you can specify the default value for each field by 
using a default method. E.g.

```java
@CaseClass
public interface Example {
    default String name() {
        return "John";
    }
}
```

If the `name()` method in the builder is not used when building the case class, the name will get the default value of "John".

## Isn't This Just Like...

There are other annotation processors that do a similar thing, e.g. [Project Lombok](https://projectlombok.org) and [Google Auto Value](https://github.com/google/auto). Soabase CaseClass does not replace those. I've recently been writing in [Scala](http://docs.scala-lang.org/tutorials/tour/case-classes.html) and really like that language's case classes. Soabase CaseClass is lightweight, has no external dependencies, and doesn't do any byte code rewriting. It has almost no options/features. It's solely focused on replicating Scala's case classes. You can use it alongside Lombok or Auto Value without any issues.

## Usage

The library is available at Maven Central:

```
groupId: io.soabase
artifactId: soabase-caseclass
```

Create an interface that is the "source" for your case class and annotate it with `@CaseClass`. Fields of the case class are declared using a no-arg method 
with a return type as the type of the field:

> FieldType fieldName();

You can declare an optional default value for the field by making it a default method:

> default FieldType fieldName() { return defaultValue; }

Soabase CaseClass is a [Java annotation processor](https://docs.oracle.com/javase/7/docs/api/javax/annotation/processing/Processor.html). When your CaseClass annotated source interface is compiled by your environment, the corresponding case class will be generated in your environment's "generated" source directory. It should get automatically included in your project as well. By default, the name of the generated case class is the same name as your source interface + _Case_. The CaseClass annotation has an attribute that you can use to change this suffix.

#### Mutability

By default, all fields in the case class are immutable. However, you can mark any field as mutable using the `@Mutable` annotation.

### Serialization

The generated case classes implement `Serializable` so they can be used with standard JDK serialization. If you'd like to use Jackson for JSON serialization, 
set the optional attribute `json` to true.

```java
@CaseClass(json = true)
public interface Example {
    String name();
}
```

## Release Notes
https://github.com/soabase/soabase-caseclass/blob/master/CHANGELOG.md

## Halva

**Welcome to Halva**

Halva's goal is to bring as many features from Scala to Java as is possible _without_ byte code generation 
or magic. Using the features in Java should be as close as possibile to how the features are used in Scala.

I wanted to see how close to Scala I could get in Java. Not only close in functionality, but close in syntax as well.
No magic, no byte code generation, nothing (too) tricky or magical. Just pure, standard Java 8. Halva is the result. The only reflection is for Google Guice's TypeLiteral class. The only "special" stuff is an annotation processor to generate case classes and type aliases. There are no dependencies whatsoever (Google Guice's TypeLiteral and JavaPoet are shaded into the code).

There are many 3rd party libraries that add support to Java in some form or another some of Scala's features. However, most of these libraries are large and use their own DSLs or syntax. I want, where possible, to take unmodified Scala (adding semicolons!!) and get it to work in Java. Of course, this isn't literally possible. But, I began to wonder how close I could actually get.
Some features are pretty trivial: some simple "sugaring" that wouldn't be hard to add. Other features, like Tuples, could be supported just as well in Java as in Scala. There are, however, a few features that are complicated or seemingly impossible in Scala: Case Classes, Pattern Matching/Extractors/Partials and Comprehensions. There's no way to get total compatibility with Scala. But, could I get 70%? 80%? I searched the net for what people think are the killer features of Scala and attempted to implement them in pure Java. Project Halva is the result.

### Full Featured Example

To see the results of what Halva can do, please look at the [Simple Interpreter Example](../../tree/master/examples/README.md).

### Features

* [Case Classes and Case Objects](../../tree/master/halva/src/main/java/io/soabase/halva/caseclass/README.md)
* [Pattern matching and extraction](../../tree/master/halva/src/main/java/io/soabase/halva/matcher/README.md)
* [For Comprehensions](../../tree/master/halva/src/main/java/io/soabase/halva/comprehension/README.md)
* [Type Aliases](../../tree/master/halva/src/main/java/io/soabase/halva/alias/README.md)
* [Tuples](../../tree/master/halva/src/main/java/io/soabase/halva/tuple/README.md)
* [Constructor Sugars](../../tree/master/halva/src/main/java/io/soabase/halva/sugar/README.md)

### Using Halva

Halva is available from [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Chalva). Use your favorite build tool and specify:

| GroupId | ArtifactId | Description |
|---------|------------|-------------|
| io.soabase.halva | halva-processor | Contains the javac processor for @CaseClass, @CaseObject and @TypeAlias |
| io.soabase.halva | halva | All the runtime code for Halva: matchers, comprehensions, etc. |


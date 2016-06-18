### *PREVIEW - ALPHA*

### Simple Interpreter Example

This is an older Scala file I found on the net here: 
  
*  http://www.scala-lang.org/old/node/56.html

The (slightly) modified version of it and the ported Java/Halva version are here:

* [SimpleInterpreter.scala](src/main/java/io/soabase/halva/examples/SimpleInterpreter.scala)
* [SimpleInterpreter.java](src/main/java/io/soabase/halva/examples/SimpleInterpreter.java)

This one small program contains all the features that people usually wish they could use in Java. To my eye, the Halva/Java version 
looks remarkably close to the Scala file. It definitely doesn't look like normal Java. Of course, it's noisier than 
the Scala version - no matter how hard you try Java is noisy. However, Java 8 is much, much, less noisier than 
previous Javas. Once you get past the imports, semicolons and some other scaffolding, it's not too bad. 

Note that good IDEs such as IntelliJ IDEA hide a lot of this noise for you - IDEA hides all the imports and 
collapses much of the other noise. This is how it looks in IDEA for me: ![IDEA View](src/main/java/io/soabase/halva/examples/IntelliJ.png?raw=true)

--------------

In particular, the interp() method looks really close to the Scala version. Let's call this a success!

![Comparison](src/main/java/io/soabase/halva/examples/compare.jpg?raw=true)

### Running this Example

Clone the Halva project and then cd into `halva/examples`.

```
> git clone https://github.com/Randgalt/halva.git
> cd halva/examples
```

_Trying the Scala version_

Start the Scala repl and load the program. Then run the test:

```
> scala
scala> :load src/main/java/io/soabase/halva/examples/SimpleInterpreter.scala
defined object simpleInterpreter
scala> simpleInterpreter.test(simpleInterpreter.term0)
res1: String = 42
scala> simpleInterpreter.test(simpleInterpreter.term1)
res2: String = wrong
```

_Trying the Java version_

```
> mvn clean package
> java -cp target/halva-examples-0.1.0.jar io.soabase.halva.examples.SimpleInterpreter
42
wrong
```

### Implementation Notes

This is the Scala program that I used to build the Halva library, feature by feature. 

* I had to make a few changes to the Scala program. Apparently, the language has changed a bit since it was written :P
* The top portion of the code is nearly identical to the Scala version. 
* Java's String class can't be subclassed so making the Name type wasn't possible.
* In the lookup() method we see the first major concession to Java. Scala allows you to define variables for 
comprehensions/pattern matching. This simply isn't possible in Java. So, Halva requires comprehension/pattern 
variables to be "defined" before being used. 
* The calls to match() and many other methods/classes in the code take advantage of static imports to make things cleaner and closer to Scala. 
* Note that when comprehension/pattern variables are used, they have to be "unboxed" via the val() method.
* In the interp() method, some variables used in the original Scala program had to be renamed because Halva's comprehension/pattern variables are all in the same scope.
* Due to the lack of covariance in Java, some class wrapping needs to be done. E.g. the Iterable() wrap and Environment() wrap in interp().
* Other than the variable definitions, the interp() method looks very much like the Scala version. **This is highly idiomatic Scala - but in Java**.

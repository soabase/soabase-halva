### Java Annotation Processing

Halva's `@CaseClass`, `@CaseObject`, `@TypeAlias`, and `@ImplicitClass` are implemented as [Java Annotation Processing](http://docs.oracle.com/javase/7/docs/technotes/guides/apt/) tools. Depending on your development environment, you may need to enable annotation processing. Here are links to how to do this for commonly used development tools:

* [IntelliJ IDEA](https://www.jetbrains.com/help/idea/2016.1/configuring-annotation-processing.html)
* [Eclipse](https://www.eclipse.org/jdt/apt/introToAPT.php)
* [NetBeans](https://netbeans.org/kb/docs/java/annotations.html)
* Maven - should do this by default. If not, you can add an execution for it. [Example Here](https://github.com/Randgalt/halva/blob/master/examples/pom.xml).
* [Gradle](http://blog.jdriven.com/2016/03/gradle-goodness-enable-compiler-annotation-processing-intellij-idea/)


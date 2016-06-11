### Tuples

A Tuple is a heterogeneous collection of items. Halva provides Tuple sizes for 0 to 16 items (named Tuple0, Tuple1, etc.). 

* All concrete Tuple classes implement the `Tuple` interface which extends `Collection<Object>`. 
* Like Scala, Tuple2 provides a `swap()` method. 
* Like Scala, Halva provides a `Pair` class that extends `Tuple2`.
* Like Scala, Tuple1 has a public field named `_1`, Tuple2 has public fields named `_1`, `_2` and so on.

#### Usage

While you can create individual Tuple types directly, you should create them as you would in Scala. Java does not allow overloading simple parenthesized expressions so Halva uses a static method named `T`. Use static imports to make this clean in your Java souce. E.g.

```java
package your.package.here;

import static io.soabase.halva.tuple.Tuple.T;

public void fooBar() {
    System.out.println("My Tuple is: " + T("one", 2, new Date()));
    
    Tuple2<String, Integer> myT2 = T("one", 2);
    Tuple2<Integer, String> swapped = myT2.swap();
}
```

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

public class Foo {
    public void fooBar() {
        System.out.println("My Tuple is: " + Tu("one", 2, new Date()));
    
        Tuple2<String, Integer> myT2 = Tu("one", 2);
        Tuple2<Integer, String> swapped = myT2.swap();
    }
}
```

#### Tuplable instead of unapply()

Scala has a magic method named unapply() that is used to deconstruct classes (in particular case classes) into a Tuple of fields (among other things). This type of magic is not possible in Java. Instead, Halva uses the `Tuplable` interface. Classes implement `Tuplable` and they can then be converted into Tuples containing the fields of the class. These can then be used in Halva's [For Comprehensions](../comprehension/README.md) and [Matching and Extracting](../matcher/README.md).

#### Tuple Equality

Tuples exist mainly for Halva's [For Comprehensions](../comprehension/README.md) and [Matching and Extracting](../matcher/README.md). Because of this, the Tuple `equals(Object)` method can behave in surpising ways. It is a loose equality checker and not an exact equality checker. In particular, calling `myTuple.equals(o)` does a deep equality check. If `o` is a single object, it is boxed into a Tuple so that Tuples always compare against other Tuples. Further, as individual items in the Tuple are compared, they are resolved/unboxed as appropriate. For example, if an item on the right of the equals sign or an item in the Tuple itself implements `Tuplable`, that item will be unboxed when comparing for equality. For example:

```java
public static class Tester implements Tuplable {
    @Override
    public Tuple tuple() {
        return Tu(1, 2);
    }
}

// this prints "true"
System.out.println(Tu(1, 2).equals(new Tester()));
```

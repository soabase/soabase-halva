### Constructor Sugars

In Scala the idiom for instantiating various objects is to use simply use the class name and then list the arguments in parenthesis. Halva's sugars provide this for common Java Library classes via the `Sugar` class. Sugar.java exposes static methods for creating Lists, Sets, etc. This is similar to the Java Librar's `Arrays.asList(...)` and similar third party libraries. However, the syntax is made just like Scala by using static importing. Sugars are provided for:

* List
* Set
* Map
* Iterator
* Iterable

E.g.

#### List

```
import static io.soabase.halva.sugar.Sugar.List;
import static io.soabase.halva.sugar.tuple.Pair.Pair;

...

List<String> myStringList = List("a", "b", "c")
List<Pair<String, Integer>> myPairs = List(Pair("10", 10), Pair("20", 20))
```

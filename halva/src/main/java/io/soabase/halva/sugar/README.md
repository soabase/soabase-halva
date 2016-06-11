### Constructor Sugars

In Scala the idiom for instantiating various objects is to use simply use the class name and then list the arguments in parenthesis. Halva's sugars provide this for common Java Library classes via the `Sugar` class. Sugar.java exposes static methods for creating Lists, Sets, etc. This is similar to the Java Librar's `Arrays.asList(...)` and similar third party libraries. However, the syntax is made just like Scala by using static importing. Sugars are provided for:

* List
* Set
* Map
* Iterator
* Iterable

Note: all instances returned by Sugar are immutable. They are also null safe - you can safely pass null arguments and will get back empty collections.

E.g.

#### List

```
import static io.soabase.halva.sugar.Sugar.List;
import static io.soabase.halva.sugar.tuple.Tuple.Pair;

...

List<String> myStringList = List("a", "b", "c");
List<Pair<String, Integer>> myPairs = List(Pair("10", 10), Pair("20", 20));
```

**Note:** The list produced is actually a `ConsList` [(see below)](#conslist).

#### Map

```
import static io.soabase.halva.sugar.Sugar.Map;
import static io.soabase.halva.sugar.tuple.Tuple.Pair;

...

Map<String, Integer> myMap = Map(Pair("10", 10), Pair("20", 20));

```

#### Set

```
import static io.soabase.halva.sugar.Sugar.Set;

...

Set<String> mySet = Set("1", "2", "3");

```

#### Iterable/Iterator

These allow wrapping single objects as Iterators or Iterables so that can be iterated over or streamed.

```
import static io.soabase.halva.sugar.Sugar.Iterable;
import static io.soabase.halva.sugar.Sugar.Iterator;

...

Iterator<String> iterator = Iterator("test");
Iterable<String> iterator = Iterable("test");


```

#### ConsList

ConsList extends the Java Library's `List` and adds these methods:

```
// return the list head (first element) or an exception if empty
T head();

// return a new list of this list minus the first element or an exception if less than 2 items
ConsList<T> tail();

// return a new list that is a concatenation of this list plus the given list
ConsList<T> concat(ConsList<T> rhs);

// return a new list that has the given item as the head of this list
ConsList<T> cons(T newHead);
```

Two static methods are provided to make cons() and conca() more natural. E.g.

```
import static io.soabase.halva.sugar.Sugar.List;
import static io.soabase.halva.sugar.Sugar.cons;
import static io.soabase.halva.sugar.Sugar.concat;

List<Integer> list1 = List(1, 2, 3, 4);
List<Integer> consList = cons(0, list1); // results in a new list: List(0, 1, 3, 4)
List<Integer> concatList = concat(list1, List(5, 6, 7)); // results in a new list: List(1, 2, 3, 4, 5, 6, 7)
List<Integer> list2 = cons(100, consList.tail()); // results in a new list: List(100, 1, 3, 4)

```

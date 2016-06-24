### For Comprehensions

----

**IMPORTANT** - First read about how Halva extracts values using [Anys](../any/README.md)

----

NOTE: If you are not familiar with Scala's for-comprehensions you should review [Scala for Java Programmers](../../../../../../../../SCALA.md) first.

------------

Halva adds support to Java for Scala's For Comprehensions feature with extraction. The syntax is as close as possible to Scala. The form is:

```
List<Value> l = forComp(any-variable, an-iterable)
    .forComp(any-variable, () -> produces-an-iterable)
...
    .yield(() -> produces-a-value);
```

For-comprehensions can be thought of as nested for loops where each nesting produces a iterator to loop over. Finally, the innermost for loop yields a value on each iteration and the comprehension collects these into a list. Another way to think of it is that each iterable given to the for-comprehension is flatMapped using the given lambda and the last iterable is mapped to a value that is collected into a list.

The full list of methods that `For` supports is:

* `<T, R> For forComp(AnyVal<T> any, Supplier<Iterable<? extends R>> stream)` - the any is a variable that will hold the result of one iteration of the given iterable. i.e. the given iterable is iterated over storing the result each time into the supplied variable.
* `For filter(SimplePredicate test)` - adds a filter to the comprehension. The items flowing through the stream only continue to the next step if they pass the predicate test.
* `<T> For letComp(AnyVal<T> any, Supplier<T> valueSupplier)` - allows setting variables at the current point in the execution.
* `<T> List<T> yield(Supplier<T> yielder)` - causes the comprehension to execute. Each iteration of the various iterables will yield the value returned by the yielder lambda.
* `void unit()` - alternate method of executing the comprehension but without returning/yielding any values.

#### Example

```java
Author aynRand = Author("Ayn Rand", List(1940, 1950));
Author kenFollet = Author("Ken Follet", List(1960, 1970, 1980));
Author leeChild = Author("Lee Child", List(1990, 2000));

List<Book> books = List(
    Book(List(aynRand, kenFollet, leeChild), "Big Compilation"),
    Book(List(aynRand), "Atlas Shrugged"),
    Book(List(kenFollet), "The Pillars of the Earth")
                       );
{
    AnyVal<Book> book = Any.make();
    AnyVal<Author> author = Any.make();
    AnyVal<Integer> year = Any.make();

    List<Tuple> result = forComp(book, books)
        .filter(() -> book.val().authors().size() == 1)
        .forComp(author, () -> book.val().authors())
        .filter(() -> author.val().name().startsWith("Ayn"))
        .forComp(year, () -> author.val().years())
        .yield(() -> Tu(book.val().title(), year.val()));

    Assert.assertEquals(List(Tu("Atlas Shrugged", 1940), Tu("Atlas Shrugged", 1950)), result);
}
```

#### Custom Implementations

Halva supports [custom For Comprehension implementations](CUSTOM.md). See the [docs](CUSTOM.md) for details.

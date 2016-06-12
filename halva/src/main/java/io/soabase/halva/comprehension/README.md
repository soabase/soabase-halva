### For Comprehensions

----

**IMPORTANT** - First read about how Halva extracts values using [Anys](../any/)

----

Halva adds support to Java for Scala's For Comprehensions feature with extraction. The syntax is as close as possible to Scala. The form is:

```
List<Value> l = For(any-variable, an-iterable)
    .and(any-variable, () -> produces-an-iterable)
...
    .yield(() -> produces-a-value);
```

For-comprehensions can be thought of as nested for loops where each nesting produces a iterator to loop over. Finally, the innermost for loop yields a value on each iteration and the comprehension collects these into a list. Another way to think of it is that each iterable given to the for-comprehension is flatMapped using the given lambda and the last iterable is mapped to a value that is collected into a list.

The full list of methods that `For` supports is:

* `<T, R> For and(Any<T> any, Supplier<Iterable<? extends R>> stream)` - the any is a variable that will hold the result of one iteration of the given iterable. i.e. the given iterable is iterated over storing the result each time into the supplied variable.
* `For when(SimplePredicate test)` - adds a filter to the comprehension. The items flowing through the stream only continue to the next step if they pass the predicate test.
* `<T> For set(Runnable value)` - a simple method for setting variables at the current point in the executiong.
* `<T> List<T> yield(Supplier<T> yielder)` - causes the comprehension to execute. Each iteration of the various iterables will yield the value returned by the yielder lambda.
* `void unit()` - alternate method of executing the comprehension but without returning/yielding any values.

#### Extraction

Extraction in for-comprehensions works exactly the same way as they do for [Pattern Matching](../matcher#extraction). Refer to the docs there.

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
    Any<Book> book = AnyDeclaration.of(Book.class).define();
    Any<Author> author = AnyDeclaration.of(Author.class).define();
    Any<Integer> year = AnyDeclaration.of(Integer.class).define();

    List<Tuple> result = For.For(book, books)
        .when(() -> book.val().authors().size() == 1)
        .and(author, () -> book.val().authors())
        .when(() -> author.val().name().startsWith("Ayn"))
        .and(year, () -> author.val().years())
        .yield(() -> T(book.val().title(), year.val()));

    Assert.assertEquals(List(T("Atlas Shrugged", 1940), T("Atlas Shrugged", 1950)), result);
}
```

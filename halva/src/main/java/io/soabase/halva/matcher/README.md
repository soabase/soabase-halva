### Pattern Matching and Extraction

----

**IMPORTANT** - First read about how Halva extracts values using [Anys](../any/)

----

Halva adds support to Java for Scala's match/case feature with extraction. The syntax is as close as possible to Scala. The form is:

```
match(arg)
    .caseOf(possibleMatch, () -> expression-when-matching
    .caseOf(anotherPossibleMatch, () -> expression-when-matching
 ...    
    .get() // gets the result
```


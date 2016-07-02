## Halva FAQ

* [Can I set attributes from the command line?](#overriding-annotation-defaults)
* [How can I use a CaseClass in another CaseClass?](#caseclasses-in-other-caseclasses)

----------

#### Overriding annotation defaults

Any Halva annotation attribute default value can be overriden by passing arguments to the Java compiler.
The form of the override is `-A_AnnotationName_._AttributeName_=defaultValue`. For example, to
change the default CaseClass suffix from "Case" to "Impl", use `-ACaseClass.suffix=Impl`.

#### CaseClasses in other CaseClasses

The Halva Annotation Processor will replace references to class templates with the name of the generated classes during processing. This allows you, for example, to have CaseClasses refer to other CaseClasses. E.g.

```java
@CaseClass public interface Name{String name()}
@CaseClass public interface Client{Name name()}
```

In the above the "name" field in Client will be of type `NameCase` in the generated ClientCase class

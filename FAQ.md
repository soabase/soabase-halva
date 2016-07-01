## Halva FAQ

* Can I set attributes from the command line?

#### Overriding annotation defaults

Any Halva annotation attribute default value can be overriden by passing arguments to the Java compiler.
The form of the override is `-A_AnnotationName_._AttributeName_=defaultValue`. For example, to
change the default CaseClass suffix from "Case" to "Impl", use `-ACaseClass.suffix=Impl`.


Unfortunately, as of Java 8, there is no library-provided TypeToken. It would not be possible
to write a library such as Halva without a TypeToken. So, a fork of Google Guice's TypeLiteral
is being used here - shaded so as not conflict with other code. It is hidden behind the AnyType
class so that it can be replaced in the future.

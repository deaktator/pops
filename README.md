# proto-macros

[proto-macros](https://github.com/deaktator/proto-macros) is designed to make working generically with protocol buffers
a simpler (and faster) experience.  Since protocol buffers make use of code generation to provide, among other things,
static methods (that can't adhere to an interface in Java), [proto-macros](https://github.com/deaktator/proto-macros) provides 
a [ProtoOps](https://github.com/deaktator/proto-macros/blob/master/proto-macros-2.4.1/src/main/scala/deaktator/proto/ProtoOps.scala)
type class that is created via implicit macros.

This allows the user to work in Scala with any `GeneratedMessage` and not have to use reflection to invoke the static methods.

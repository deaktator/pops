# Pops

[Pops](https://github.com/deaktator/pops) (Protocol Buffer Ops) makes working generically with protocol buffers
a simpler (and faster) experience.  



Protocol buffers (*PB*) make use of code generation to provide, among other things, static methods.  These methods can't adhere to an interface in Java. [Pops](https://github.com/deaktator/pops) eases this burden by providing type classes and a way to easily create typed accessor functions to extract data from [`com.google.protobuf.GeneratedMessage`](https://developers.google.com/protocol-buffers/docs/reference/java/com/google/protobuf/GeneratedMessage)s.  

The code is designed to use compile-time reflection ([macros](http://docs.scala-lang.org/overviews/macros/overview.html)) and more specifically [implicit materialization](http://docs.scala-lang.org/overviews/macros/implicits) to avoid the cost of runtime reflection present in most solutions.  There are runtime variant available for those cases when absolutely necessary, for instance when creating a `Class` object from a `Class.forName` call.  However, if you know the types at compile-time, the macro-based versions are considerably faster.

## Classes of interest

- [ProtoOps](https://github.com/deaktator/pops/blob/master/pops-2.4.1/src/main/scala/deaktator/pops/msgs/ProtoOps.scala): a
type class for working with [`com.google.protobuf.GeneratedMessage`](https://developers.google.com/protocol-buffers/docs/reference/java/com/google/protobuf/GeneratedMessage)s.
- [EnumProtoOps](https://github.com/deaktator/pops/blob/master/pops-2.4.1/src/main/scala/deaktator/pops/enums/EnumProtoOps.scala): a type class for working with [`com.google.protobuf.ProtocolMessageEnum`](https://developers.google.com/protocol-buffers/docs/reference/java/com/google/protobuf/ProtocolMessageEnum)s.
- [ProtoAccessor](https://github.com/deaktator/pops/blob/master/pops-2.4.1/src/main/scala/deaktator/pops/fn/ProtoAccessor.scala) a trait extending [`scala.Function1`](http://www.scala-lang.org/api/current/index.html#scala.Function1) that can be created from a string-based path into the *PB* instance.


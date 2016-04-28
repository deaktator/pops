package deaktator.pops.msgs

import java.io.InputStream

import com.google.protobuf.Descriptors.Descriptor
import com.google.protobuf._

import scala.reflect.macros.blackbox

/**
  * A [[http://docs.scala-lang.org/overviews/macros/overview.html macro-based]] [[ProtoOps]] implementation
  * that uses implicit materialization. All that needs to be done to use it is to request an instance via
  * {{{
  * import com.google.protobuf.GeneratedMessage
  * import deaktator.proto.msgs.ProtoOps
  * import com.eharmony.aloha.score.Scores.Score // Or some other PB instance.
  * val myInstance: ProtoOps[Score]
  *
  * // or include it as an implicit parameter in a function or class
  * def getDefault[A <: GeneratedMessage](implicit ops: ProtoOps[A]): A = ops.getDefaultInstance()
  * }}}
  * @author deaktator
  */
@macrocompat.bundle
private[msgs] class ProtoOpsMacros(val c: blackbox.Context) {
  def materialize[A <: GeneratedMessage: c.WeakTypeTag]: c.Expr[ProtoOps[A]] = {
    import c.universe._

    val a = weakTypeOf[A]
    val protoOps = weakTypeOf[ProtoOps[A]]
    val serializable = weakTypeOf[Serializable]
    val proto = a.companion

    val descriptor = weakTypeOf[Descriptor]
    val byteString = weakTypeOf[ByteString]
    val extensionRegistryLite = weakTypeOf[ExtensionRegistryLite]
    val codedInputStream = weakTypeOf[CodedInputStream]
    val inputStream = weakTypeOf[InputStream]

    c.Expr[ProtoOps[A]] {
      q"""new $protoOps with $serializable {
            def getDefaultInstance(): $a = $proto.getDefaultInstance()
            def getDescriptor(): $descriptor = $proto.getDescriptor()
            def parseFrom(data: $byteString): $a = $proto.parseFrom(data)
            def parseFrom(data: $byteString, extensionRegistry: $extensionRegistryLite): $a =
              $proto.parseFrom(data, extensionRegistry)
            def parseFrom(data: Array[Byte]): $a = $proto.parseFrom(data)
            def parseFrom(data: Array[Byte], extensionRegistry: $extensionRegistryLite): $a =
              $proto.parseFrom(data, extensionRegistry)
            def parseFrom(input: $inputStream): $a = $proto.parseFrom(input)
            def parseFrom(input: $inputStream, extensionRegistry: $extensionRegistryLite): $a =
              $proto.parseFrom(input, extensionRegistry)
            def parseFrom(input: $codedInputStream): $a = $proto.parseFrom(input)
            def parseFrom(input: $codedInputStream, extensionRegistry: $extensionRegistryLite): $a =
              $proto.parseFrom(input, extensionRegistry)
            def parseDelimitedFrom(input: $inputStream): $a = $proto.parseDelimitedFrom(input)
            def parseDelimitedFrom(input: $inputStream, extensionRegistry: $extensionRegistryLite):
              $a = $proto.parseDelimitedFrom(input, extensionRegistry)
          }
       """
    }
  }

  def materializeLite[A <: GeneratedMessageLite: c.WeakTypeTag]: c.Expr[ProtoLiteOps[A]] = {
    import c.universe._

    val a = weakTypeOf[A]
    val protoOps = weakTypeOf[ProtoLiteOps[A]]
    val serializable = weakTypeOf[Serializable]
    val proto = a.companion

    val byteString = weakTypeOf[ByteString]
    val extensionRegistryLite = weakTypeOf[ExtensionRegistryLite]
    val codedInputStream = weakTypeOf[CodedInputStream]
    val inputStream = weakTypeOf[InputStream]

    c.Expr[ProtoLiteOps[A]] {
      q"""new $protoOps with $serializable {
            def getDefaultInstance(): $a = $proto.getDefaultInstance()
            def parseFrom(data: $byteString): $a = $proto.parseFrom(data)
            def parseFrom(data: $byteString, extensionRegistry: $extensionRegistryLite): $a =
              $proto.parseFrom(data, extensionRegistry)
            def parseFrom(data: Array[Byte]): $a = $proto.parseFrom(data)
            def parseFrom(data: Array[Byte], extensionRegistry: $extensionRegistryLite): $a =
              $proto.parseFrom(data, extensionRegistry)
            def parseFrom(input: $inputStream): $a = $proto.parseFrom(input)
            def parseFrom(input: $inputStream, extensionRegistry: $extensionRegistryLite): $a =
              $proto.parseFrom(input, extensionRegistry)
            def parseFrom(input: $codedInputStream): $a = $proto.parseFrom(input)
            def parseFrom(input: $codedInputStream, extensionRegistry: $extensionRegistryLite): $a =
              $proto.parseFrom(input, extensionRegistry)
            def parseDelimitedFrom(input: $inputStream): $a = $proto.parseDelimitedFrom(input)
            def parseDelimitedFrom(input: $inputStream, extensionRegistry: $extensionRegistryLite):
              $a = $proto.parseDelimitedFrom(input, extensionRegistry)
          }
       """
    }
  }
}

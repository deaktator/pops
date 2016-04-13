package deaktator.proto

import java.io.InputStream

import com.google.protobuf.Descriptors.Descriptor
import com.google.protobuf.{ByteString, CodedInputStream, ExtensionRegistryLite, GeneratedMessage}
import org.apache.commons.codec.binary.Base64

import scala.reflect.macros.blackbox

/**
  * Created by ryan on 4/12/16.
  */
@macrocompat.bundle
class ProtoOpsMacros(val c: blackbox.Context) {
  def materialize[A <: GeneratedMessage: c.WeakTypeTag]: c.Expr[ProtoOps[A]] = {
    import c.universe._

    val a = weakTypeOf[A]
    val proto = a.companion
    val b64 = weakTypeOf[Base64].companion

    val descriptor = weakTypeOf[Descriptor]
    val byteString = weakTypeOf[ByteString]
    val extensionRegistryLite = weakTypeOf[ExtensionRegistryLite]
    val codedInputStream = weakTypeOf[CodedInputStream]
    val inputStream = weakTypeOf[InputStream]

    c.Expr[ProtoOps[A]] {
      q"""new ProtoOps[$a] with Serializable {
            def parseFromB64(s: String): $a = $proto.parseFrom($b64.decodeBase64(s))
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
}

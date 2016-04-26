package deaktator.proto.msgs

import java.io.{IOException, InputStream}

import com.google.protobuf.Descriptors.Descriptor
import com.google.protobuf.{ByteString, CodedInputStream, ExtensionRegistryLite, GeneratedMessage, InvalidProtocolBufferException}
import org.apache.commons.codec.binary.Base64

/**
  * A Runtime-based version of [[ProtoOps]].  This really should be used unless necessary.  For instance,
  * if calling `Class.forName` isn't necessary, this should really be either.  In which case, the preferable
  * way to get a [[ProtoOps]] instance is implicitly via the implicit factory method in the [[ProtoOps]]
  * companion object.
  */
private[proto] final case class RuntimeProtoOps[A <: GeneratedMessage](messageClass: Class[A]) extends ProtoOps[A] {
  def parseFromB64(s: String): A =
    messageClass.getMethod("parseFrom", classOf[Array[Byte]]).invoke(null, Base64.decodeBase64(s)).asInstanceOf[A]

  def getDefaultInstance(): A =
    messageClass.getMethod("getDefaultInstance").invoke(null).asInstanceOf[A]

  def getDescriptor(): Descriptor =
    messageClass.getMethod("getDescriptor").invoke(null).asInstanceOf[Descriptor]

  @throws(classOf[InvalidProtocolBufferException])
  def parseFrom(data: ByteString): A =
    messageClass.getMethod("parseFrom", classOf[ByteString]).invoke(null, data).asInstanceOf[A]

  @throws(classOf[InvalidProtocolBufferException])
  def parseFrom(data: ByteString, extensionRegistry: ExtensionRegistryLite): A =
    messageClass.getMethod("parseFrom", classOf[ByteString], classOf[ExtensionRegistryLite]).
                 invoke(null, data, extensionRegistry).asInstanceOf[A]

  @throws(classOf[InvalidProtocolBufferException])
  def parseFrom(data: Array[Byte]): A =
    messageClass.getMethod("parseFrom", classOf[Array[Byte]]).invoke(null, data).asInstanceOf[A]

  @throws(classOf[InvalidProtocolBufferException])
  def parseFrom(data: Array[Byte], extensionRegistry: ExtensionRegistryLite): A =
    messageClass.getMethod("parseFrom", classOf[Array[Byte]], classOf[ExtensionRegistryLite]).
                 invoke(null, data, extensionRegistry).asInstanceOf[A]

  @throws(classOf[IOException])
  def parseFrom(input: InputStream): A =
    messageClass.getMethod("parseFrom", classOf[InputStream]).invoke(null, input).asInstanceOf[A]

  @throws(classOf[IOException])
  def parseFrom(input: InputStream, extensionRegistry: ExtensionRegistryLite): A =
    messageClass.getMethod("parseFrom", classOf[InputStream], classOf[ExtensionRegistryLite]).
                 invoke(null, input, extensionRegistry).asInstanceOf[A]

  @throws(classOf[IOException])
  def parseDelimitedFrom(input: InputStream): A =
    messageClass.getMethod("parseDelimitedFrom", classOf[InputStream]).invoke(null, input).asInstanceOf[A]

  @throws(classOf[IOException])
  def parseDelimitedFrom(input: InputStream, extensionRegistry: ExtensionRegistryLite): A =
    messageClass.getMethod("parseDelimitedFrom", classOf[InputStream], classOf[ExtensionRegistryLite]).
                 invoke(null, input, extensionRegistry).asInstanceOf[A]

  @throws(classOf[IOException])
  def parseFrom(input: CodedInputStream): A =
    messageClass.getMethod("parseFrom", classOf[CodedInputStream]).invoke(null, input).asInstanceOf[A]

  @throws(classOf[IOException])
  def parseFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): A =
    messageClass.getMethod("parseFrom", classOf[CodedInputStream], classOf[ExtensionRegistryLite]).
                 invoke(null, input, extensionRegistry).asInstanceOf[A]
}

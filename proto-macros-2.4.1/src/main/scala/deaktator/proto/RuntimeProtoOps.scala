package deaktator.proto

import com.google.protobuf.GeneratedMessage
import org.apache.commons.codec.binary.Base64

import com.google.protobuf.ByteString
import com.google.protobuf.InvalidProtocolBufferException
import com.google.protobuf.Descriptors.Descriptor
import com.google.protobuf.ExtensionRegistryLite
import com.google.protobuf.CodedInputStream
import java.io.InputStream
import java.io.IOException



/**
  * Created by ryan on 4/13/16.
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

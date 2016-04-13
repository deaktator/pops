package deaktator.proto

import java.io.{IOException, InputStream}

import com.google.protobuf.Descriptors.Descriptor
import com.google.protobuf.{ByteString, CodedInputStream, ExtensionRegistryLite, GeneratedMessage, InvalidProtocolBufferException}

import scala.annotation.implicitNotFound
import scala.language.experimental.macros

/**
  * Created by ryan on 4/11/16.
  */
@implicitNotFound(msg = "Cannot find ProtoOps type class for ${A}.")
trait ProtoOps[A <: GeneratedMessage] {
  def parseFromB64(s: String): A

  def getDefaultInstance(): A

  def getDescriptor(): Descriptor

  @throws(classOf[InvalidProtocolBufferException])
  def parseFrom(data: ByteString ): A

  @throws(classOf[InvalidProtocolBufferException])
  def parseFrom(data: ByteString, extensionRegistry: ExtensionRegistryLite): A

  @throws(classOf[InvalidProtocolBufferException])
  def parseFrom(data: Array[Byte]): A

  @throws(classOf[InvalidProtocolBufferException])
  def parseFrom(data: Array[Byte], extensionRegistry: ExtensionRegistryLite): A

  @throws(classOf[IOException])
  def parseFrom(input: InputStream): A

  @throws(classOf[IOException])
  def parseFrom(input: InputStream, extensionRegistry: ExtensionRegistryLite): A

  @throws(classOf[IOException])
  def parseDelimitedFrom(input: InputStream): A

  @throws(classOf[IOException])
  def parseDelimitedFrom(input: InputStream, extensionRegistry: ExtensionRegistryLite): A

  @throws(classOf[IOException])
  def parseFrom(input: CodedInputStream): A

  @throws(classOf[IOException])
  def parseFrom(input: CodedInputStream, extensionRegistry: ExtensionRegistryLite): A
}

object ProtoOps {
  /**
    * Materialize a ProtoOps[A] using macros
    * @tparam A type of the generated message to create.
    * @return
    */
  implicit def apply[A <: GeneratedMessage]: ProtoOps[A] = macro ProtoOpsMacros.materialize[A]

  /**
    * This really only should be used in cases where compile-time type knowledge is not available.
    * @param c Class[A]
    * @tparam A
    * @return
    */
  def runtime[A <: GeneratedMessage](c: Class[A]): ProtoOps[A] = RuntimeProtoOps(c)
}


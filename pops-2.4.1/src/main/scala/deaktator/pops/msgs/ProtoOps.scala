package deaktator.pops.msgs

import java.io.{IOException, InputStream}

import com.google.protobuf.Descriptors.Descriptor
import com.google.protobuf._

import scala.annotation.implicitNotFound
import scala.language.experimental.macros
import scala.language.implicitConversions

/**
  * A type class for static methods present in `com.google.protobuf.GeneratedMessage`.
  * @author deaktator
  */
@implicitNotFound(msg = "Cannot find ProtoOps type class for ${A}.")
trait ProtoOps[A <: GeneratedMessage] {
  def parseFromB64(s: String): A

  def getDefaultInstance(): A

  def getDescriptor(): Descriptor

  @throws(classOf[InvalidProtocolBufferException])
  def parseFrom(data: ByteString): A

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

/**
  * Provides factory methods and implicit materializer macros to Get class instances.
  * @author deaktator
  */
object ProtoOps {

  /**
    * Materialize a `ProtoOps[A]` using macros.  This is the one of the two prefered ways to
    * get one an instance of [[ProtoOps]].  The other is using [[deaktator.pops.Proto.apply]].
    * @tparam A type of the `GeneratedMessage` for which a type class should be materialized.
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


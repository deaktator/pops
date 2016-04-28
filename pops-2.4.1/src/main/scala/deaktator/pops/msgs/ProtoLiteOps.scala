package deaktator.pops.msgs

import java.io.{InputStream, IOException}

import com.google.protobuf._

import scala.annotation.implicitNotFound
import scala.language.experimental.macros

/**
  * Created by deak on 4/28/16.
  */
@implicitNotFound(msg = "Cannot find ProtoLiteOps type class for ${A}.")
trait ProtoLiteOps[A] {
  def getDefaultInstance(): A

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

object ProtoLiteOps {

  /**
    * Materialize a `ProtoOps[A]` using macros.  This is the one of the two prefered ways to
    * get one an instance of [[ProtoOps]].  The other is using [[deaktator.pops.Proto.apply]].
    * @tparam A type of the `GeneratedMessage` for which a type class should be materialized.
    * @return
    */
  implicit def apply[A <: GeneratedMessageLite]: ProtoLiteOps[A] = macro ProtoOpsMacros.materializeLite[A]

  /**
    * Materialize a `ProtoOps[A]` using macros.  This is the one of the two prefered ways to
    * get one an instance of [[ProtoOps]].  The other is using [[deaktator.pops.Proto.apply]].
    * @tparam A type of the `GeneratedMessage` for which a type class should be materialized.
    * @return
    */
  def runtime[A <: GeneratedMessageLite](c: Class[A]): ProtoLiteOps[A] = RuntimeProtoLiteOpsImpl(c)
}

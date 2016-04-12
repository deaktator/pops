package deaktator.proto

import com.google.protobuf.GeneratedMessage
import org.apache.commons.codec.binary.Base64

import scala.annotation.implicitNotFound
import scala.language.experimental.macros
import scala.util.Try

/**
  * Created by ryan on 4/11/16.
  */
@implicitNotFound(msg = "Cannot find ProtoOps type class for ${A}.")
trait ProtoOps[A <: GeneratedMessage] {
  def parseFromB64(s: String): A
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

  private[this] case class RuntimeProtoOps[A <: GeneratedMessage](messageClass: Class[A]) extends ProtoOps[A] {
    override def parseFromB64(s: String): A = {
      messageClass.getMethod("parseFrom", classOf[Array[Byte]]).invoke(null, Base64.decodeBase64(s)).asInstanceOf[A]
    }
  }
}


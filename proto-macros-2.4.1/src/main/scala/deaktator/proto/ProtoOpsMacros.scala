package deaktator.proto

import com.google.protobuf.GeneratedMessage
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

    c.Expr[ProtoOps[A]] {
      q"""new ProtoOps[$a] with Serializable {
            def parseFromB64(s: String): $a = $proto.parseFrom($b64.decodeBase64(s))
          }
       """
    }
  }
}
package deaktator.proto.enums

import com.google.protobuf.Descriptors.{EnumDescriptor, EnumValueDescriptor}
import com.google.protobuf.Internal.EnumLiteMap
import com.google.protobuf.ProtocolMessageEnum

import scala.reflect.macros.blackbox

/**
  *
  */
@macrocompat.bundle
class EnumProtoOpsMacros(val c: blackbox.Context) {
  def materialize[A <: ProtocolMessageEnum : c.WeakTypeTag]: c.Expr[EnumProtoOps[A]] = {
    import c.universe._

    val enumProtoOps = weakTypeOf[EnumProtoOps[A]]
    val a = weakTypeOf[A]
    val enm = a.companion
    val enumDescriptor = weakTypeOf[EnumDescriptor]
    val enumValueDescriptor = weakTypeOf[EnumValueDescriptor]
    val enumLiteMap = weakTypeOf[EnumLiteMap[A]]
    val serializable = weakTypeOf[Serializable]

    c.Expr[EnumProtoOps[A]] {
      q"""new $enumProtoOps with $serializable {
            def getDescriptor(): $enumDescriptor = $enm.getDescriptor()
            def internalGetValueMap(): $enumLiteMap = $enm.internalGetValueMap()
            def valueOf(value: Int): $a = $enm.valueOf(value)
            def valueOf(desc: $enumValueDescriptor): $a = $enm.valueOf(desc)
          }
       """
    }
  }
}
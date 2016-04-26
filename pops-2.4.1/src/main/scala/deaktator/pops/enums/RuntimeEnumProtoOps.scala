package deaktator.pops.enums

import com.google.protobuf.Descriptors.{EnumDescriptor, EnumValueDescriptor}
import com.google.protobuf.Internal.EnumLiteMap
import com.google.protobuf.ProtocolMessageEnum

/**
  * A Runtime-based version of [[EnumProtoOps]].  This really should be used unless necessary.  For instance,
  * if calling `Class.forName` isn't necessary, this should really be either.  In which case, the preferable
  * way to get an [[EnumProtoOps]] instance is implicitly via the implicit factory method in
  * the [[EnumProtoOps]] companion object.
  * @author deaktator
  */
private[pops] final case class RuntimeEnumProtoOps[A <: ProtocolMessageEnum](enumClass: Class[A]) extends EnumProtoOps[A] {
  def getDescriptor(): EnumDescriptor =
    enumClass.getMethod("getDescriptor").invoke(null).asInstanceOf[EnumDescriptor]
  def internalGetValueMap(): EnumLiteMap[A] =
    enumClass.getMethod("internalGetValueMap").invoke(null).asInstanceOf[EnumLiteMap[A]]
  def valueOf(value: Int): A =
    enumClass.getMethod("valueOf", classOf[Integer]).invoke(null, Integer.valueOf(value)).asInstanceOf[A]
  def valueOf(desc: EnumValueDescriptor): A =
    enumClass.getMethod("valueOf", classOf[EnumValueDescriptor]).invoke(null, desc).asInstanceOf[A]
}

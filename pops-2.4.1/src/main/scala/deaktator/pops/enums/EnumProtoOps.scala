package deaktator.pops.enums

import com.google.protobuf.Descriptors.{EnumDescriptor, EnumValueDescriptor}
import com.google.protobuf.Internal.EnumLiteMap
import com.google.protobuf.ProtocolMessageEnum

import scala.language.experimental.macros

/**
  * A type class for static methods present in `com.google.protobuf.ProtocolMessageEnum`.
  * @author deaktator
  */
trait EnumProtoOps[A <: ProtocolMessageEnum] {
  def getDescriptor(): EnumDescriptor
  def internalGetValueMap(): EnumLiteMap[A]
  def valueOf(value: Int): A
  def valueOf(desc: EnumValueDescriptor): A
}

/**
  * Provides factory methods and implicit materializer macros to Get class instances.
  * @author deaktator
  */
object EnumProtoOps {

  /**
    * Materialize an `EnumProtoOps[A]` using macros.
    * @tparam A the type of the enumerated type for which a type class instance should be materialized.
    * @return
    */
  implicit def apply[A <: ProtocolMessageEnum]: EnumProtoOps[A] = macro EnumProtoOpsMacros.materialize[A]
}


package deaktator.pops.fn

import com.google.protobuf.Descriptors.FieldDescriptor.JavaType.{BOOLEAN, BYTE_STRING, DOUBLE, ENUM, FLOAT, INT, LONG, MESSAGE, STRING}
import com.google.protobuf.Descriptors.{EnumValueDescriptor, FieldDescriptor}
import com.google.protobuf.{ByteString, GeneratedMessage, ProtocolMessageEnum}
import deaktator.pops.enums.EnumProtoOps
import deaktator.pops.msgs.ProtoOps

/**
  * A caster takes the untyped value returned by the PB reflection APIs and provides a
  * typesafe value.  It is also responsible for determining whether a path adhere's to
  * the desired stated type in the [[ProtoAccessor]] factory methods.
  * @tparam A type of `GeneratedMessage` from which data should be extracted.
  * @tparam B type of data to be extracted from `A`.
  * @author deaktator
  */
// TODO: Make API with B covariant
sealed trait Caster[A <: GeneratedMessage, B] extends Serializable {

  /**
    * Determine if there is an error in creating a [[ProtoAccessor]].  If there is an error,
    * an error message will be returned.
    * @param path raw string-based path representation
    * @param fds `FieldDescriptor`-based path representation
    * @param ops possible used for type checking.
    * @return an error message in a Some if there was an error; otherwise, None.
    */
  def error(path: String, fds: ::[FieldDescriptor])(implicit ops: ProtoOps[A]): Option[String]

  /**
    * Cast the untyped `value` returned by the PB reflection API and give it a proper type.
    * @param hasField whether the field was present in the PB instance (true) or it is a
    *                 default value (false).
    * @param value the value to cast.
    * @return a typed version of `value`.
    */
  def cast(hasField: Boolean, value: Any): B
}

// TODO: When B is covariant, need lower priority implicits.
private[pops] object Caster extends Serializable {

  type GM = GeneratedMessage
  type PME = ProtocolMessageEnum

  def apply[A <: GM, B](implicit c: Caster[A, B]): Caster[A, B] = c

  implicit def booleanCaster[A <: GM]: Caster[A, Boolean] = req[A, Boolean](BOOLEAN)
  implicit def booleanOptCaster[A <: GM]: Caster[A, Option[Boolean]] = opt[A, Boolean](BOOLEAN)

  implicit def byteStringCaster[A <: GM]: Caster[A, ByteString] = req[A, ByteString](BYTE_STRING)
  implicit def byteStringOptCaster[A <: GM]: Caster[A, Option[ByteString]] = opt[A, ByteString](BYTE_STRING)

  implicit def doubleCaster[A <: GM]: Caster[A, Double] = req[A, Double](DOUBLE)
  implicit def doubleOptCaster[A <: GM]: Caster[A, Option[Double]] = opt[A, Double](DOUBLE)

  implicit def enumCaster[A <: GM, B <: PME with Enum[B] : EnumProtoOps]: Caster[A, B] = ReqEnum[A, B]
  implicit def enumOptCaster[A <: GM, B <: PME with Enum[B] : EnumProtoOps]: Caster[A, Option[B]] = OptEnum[A, B]

  implicit def floatCaster[A <: GM]: Caster[A, Float] = req[A, Float](FLOAT)
  implicit def floatOptCaster[A <: GM]: Caster[A, Option[Float]] = opt[A, Float](FLOAT)

  implicit def intCaster[A <: GM]: Caster[A, Int] = req[A, Int](INT)
  implicit def intOptCaster[A <: GM]: Caster[A, Option[Int]] = opt[A, Int](INT)

  implicit def longCaster[A <: GM]: Caster[A, Long] = req[A, Long](LONG)
  implicit def longOptCaster[A <: GM]: Caster[A, Option[Long]] = opt[A, Long](LONG)

  implicit def stringCaster[A <: GM]: Caster[A, String] = req[A, String](STRING)
  implicit def stringOptCaster[A <: GM]: Caster[A, Option[String]] = opt[A, String](STRING)

  implicit def msgCaster[A <: GM, B <: GM]: Caster[A, B] = req[A, B](MESSAGE)
  implicit def msgOptCaster[A <: GM, B <: GM]: Caster[A, Option[B]] = opt[A, B](MESSAGE)

  // TODO create anyCaster that is hybrid of req / reqEnum

  private[this] def req[A <: GM, B](fds: FieldDescriptor.JavaType*): Caster[A, B] = Req[A, B](Set(fds:_*))
  private[this] def opt[A <: GM, B](fds: FieldDescriptor.JavaType*): Caster[A, Option[B]] = Opt[A, B](Set(fds:_*))

  private[pops] def wrongType(fds: ::[FieldDescriptor], acceptable: Set[FieldDescriptor.JavaType]): Option[String] = {
    val fd = fds.last
    val t = fd.getJavaType
    if (acceptable contains t)
      None
    else Option(s"${fd.getFullName} is wrong type.  Found $t, expected one of ${acceptable.mkString("{", ", ", "}")}.")
  }

  private[pops] def notEnum(fds: ::[FieldDescriptor]): Option[String] = {
    val fd = fds.last
    val t = fd.getJavaType
    if (ENUM == t)
      None
    else Option(s"${fd.getFullName} is not an enum.  Found $t.")
  }

  private[pops] def notRequired(fds: ::[FieldDescriptor]): Option[String] =
    fds.find(fd => !fd.isRequired).map(fd => s"${fd.getFullName} is not a required field. ProtoAccessor output type should be an Option." )

  private[pops] def notOptional(fds: ::[FieldDescriptor]): Option[String] =
    fds.find(fd => !(fd.isRequired || fd.isOptional)).map(fd => s"${fd.getFullName} is not an optional or required field." )

  private[pops] def addPath(path: String, msg: Option[String]) =
    msg map (_ + s"""  Path: "$path".""")

  private[pops] case class ReqEnum[A <: GM, B <: PME](implicit private val epo: EnumProtoOps[B]) extends Caster[A, B] {
    override def cast(hasField: Boolean, value: Any): B = epo.valueOf(value.asInstanceOf[EnumValueDescriptor])
    override def error(path: String, fds: ::[FieldDescriptor])(implicit ops: ProtoOps[A]): Option[String] =
      addPath(path, notRequired(fds) orElse notEnum(fds))
  }

  private[pops] case class OptEnum[A <: GM, B <: PME](implicit private val epo: EnumProtoOps[B]) extends Caster[A, Option[B]] {
    override def cast(hasField: Boolean, value: Any): Option[B] =
      if (hasField)
        Option(value) flatMap {
          case d: EnumValueDescriptor => Option(epo.valueOf(d))
          case _ => None
        }
      else None

    override def error(path: String, fds: ::[FieldDescriptor])(implicit ops: ProtoOps[A]): Option[String] =
      addPath(path, notOptional(fds) orElse notEnum(fds))
  }

  private[pops] case class Req[A <: GM, B](validTypes: Set[FieldDescriptor.JavaType]) extends Caster[A, B] {
    override def cast(hasField: Boolean, value: Any): B = value.asInstanceOf[B]
    override def error(path: String, fds: ::[FieldDescriptor])(implicit ops: ProtoOps[A]): Option[String] =
      addPath(path, notRequired(fds) orElse wrongType(fds, validTypes))
  }

  private[this] case class Opt[A <: GM, B](validTypes: Set[FieldDescriptor.JavaType]) extends Caster[A, Option[B]] {
    override def cast(hasField: Boolean, value: Any): Option[B] =
      if (hasField)
        Option(value).asInstanceOf[Option[B]]
      else None
    override def error(path: String, fds: ::[FieldDescriptor])(implicit ops: ProtoOps[A]): Option[String] =
      addPath(path, notOptional(fds) orElse wrongType(fds, validTypes))
  }
}

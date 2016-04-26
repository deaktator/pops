package deaktator.pops.fn

import java.io._

import com.google.protobuf.Descriptors.FieldDescriptor.JavaType.MESSAGE
import com.google.protobuf.Descriptors.{Descriptor, FieldDescriptor}
import com.google.protobuf.GeneratedMessage
import deaktator.pops.msgs.ProtoOps

import scala.annotation.tailrec
import scala.language.implicitConversions

/**
  * A representation of an accessor function whose domain is something extending Protocol Buffers'
  * `GenerateMessage`.
  * A trait that extends function to remove contravariance on `A` and covariance on `B`.
  * This eases implicit search and therefore conversion from string-based paths.
  * '''Note''': only `ProtoAccessor.ProtoAccessorImpl` implements this trait.
  * @tparam A type of `GeneratedMessage` from which data should be extracted.
  * @tparam B type of data to be extracted from `A`.
  * @author deaktator
  */
sealed trait ProtoAccessor[A <: GeneratedMessage, B] extends (A => B) with Serializable

/**
  * Contains `either` and `option` factory methods to produce [[ProtoAccessor]] from string-based
  * paths.  Note that conversions can be called explicitly or can be used implicitly.  In the
  * following examples, `Score`'s definition can be found at
  * [[https://github.com/eHarmony/aloha-proto/blob/master/src/main/proto/com.eharmony.aloha.score.Scores.proto aloha-proto/Scores.proto]].
  *
  * {{{
  * import com.eharmony.aloha.score.Scores.Score
  *
  * // Explicit
  * val a1: Either[String, ProtoAccessor[Score, Option[Long]]] =
  *   ProtoAccessor.either[Score, Option[Long]]("error.model.id")
  *
  * // a2: Either[String, ProtoAccessor[Score, Option[Long]]]
  * val a2 = ProtoAccessor.either[Score, Option[Long]]("error.model.id")
  *
  * // Implicit
  * val a3: Either[String, ProtoAccessor[Score, Option[String]]] = "error.model.name"
  *
  * // Implicit, many at a time ...
  * val as: Seq[Either[String, ProtoAccessor[Score, Option[Long]]]] =
  *   Seq("error.model.id", "error.model.id")
  * }}}
  *
  * If error information isn't important, use the Option-based API:
  *
  * {{{
  * import com.eharmony.aloha.score.Scores.Score
  *
  * // Explicit
  *
  * val a1: Option[ProtoAccessor[Score, Option[Long]]] =
  *   ProtoAccessor.option[Score, Option[Long]]("error.model.id")
  *
  * // Implicit
  *
  * val a3: Option[ProtoAccessor[Score, Option[String]]] = "error.model.name"
  * }}}
  *
  * An attempt is made to fully respect types so the following won't work because
  * "error.model.id" is represented by a `Long` in the protobuf definition:
  *
  * {{{
  * // Returns error message on the Left.
  * import com.eharmony.aloha.score.Scores.Score
  * val a1: Either[String, ProtoAccessor[Score, Option[Int]]] = "error.model.id"
  * }}}
  *
  * Additionally, Option and non-Option based accessors are respected.  If a non-`required` path
  * element is found in the path, and the accessor doesn't return an Option, an error will be
  * returned:
  *
  * {{{
  * // Returns error message on the Left because "error" is optional in the PB definition.
  * import com.eharmony.aloha.score.Scores.Score
  * val a1: Either[String, ProtoAccessor[Score, Long]] = "error.model.id"
  * }}}
  *
  * @author deaktator
  */
// TODO: Figure out how to make implicits work when `B` is covariant.
object ProtoAccessor {

  /**
    * Attempt to create a protobuf accessor: `Right(ProtoAccessor[A, B])`.  If an error is encountered
    * Left will contain the error message.
    * @param path the path string
    * @param ops an instance of [[deaktator.pops.msgs.ProtoOps]] to help with accessor function creation.
    * @param c a caster that takes the untyped value returned by the PB reflection APIs and provides
    *          a typesafe value.
    * @tparam A type of `GeneratedMessage` from which data should be extracted.
    * @tparam B type of data to be extracted from `A`.
    * @return an accessor on the right or an error message on the left.
    */
  implicit def either[A <: GeneratedMessage, B](path: String)(implicit ops: ProtoOps[A], c: Caster[A, B]): Either[String, ProtoAccessor[A, B]] =
    buildProtoAccessor[A, B](path, splitPath(path), Nil, ops.getDescriptor())

  /**
    * Attempt to create a protobuf accessor: `Some(ProtoAccessor[A, B])`.  If an error is encountered
    * None is returned.  This doesn't provide diagnostic information like [[ProtoAccessor.either]] does.
    * @param path the path string
    * @param ops an instance of [[deaktator.pops.msgs.ProtoOps]] to help with accessor function creation.
    * @param c a caster that takes the untyped value returned by the PB reflection APIs and provides
    *          a typesafe value.
    * @tparam A type of `GeneratedMessage` from which data should be extracted.
    * @tparam B type of data to be extracted from `A`.
    * @return an accessor or None if an error is encountered.
    */
  implicit def option[A <: GeneratedMessage, B](path: String)(implicit ops: ProtoOps[A], c: Caster[A, B]): Option[ProtoAccessor[A, B]] =
    either[A, B](path).right.toOption

  /**
    * The internal function used to generate the [[ProtoAccessor]].
    * @param pathStr the path string
    * @param path the remainder of the path elements to traverse.
    * @param fds the `FieldDescriptor`s in reverse order of the `path` elements already traversed.
    * @param d the current PB `Descriptor`
    * @param c a caster that takes the untyped value returned by the PB reflection APIs and provides
    *          a typesafe value.
    * @tparam A type of `GeneratedMessage` from which data should be extracted.
    * @tparam B type of data to be extracted from `A`.
    * @return an accessor on the right or an error message on the left.
    */
  @tailrec private[pops] def buildProtoAccessor[A <: GeneratedMessage, B](
      pathStr: String,
      path: List[String],
      fds: List[FieldDescriptor],
      d: Descriptor)(implicit
      ops: ProtoOps[A],
      c: Caster[A, B]): Either[String, ProtoAccessor[A, B]] = {

    path match {
      case Nil => Left(s"""ProtoAccessor path is empty. Provided: "$pathStr"""")
      case h :: Nil =>
        Option(d.findFieldByName(h)) map { fd =>
          (fd :: fds).reverse match {
            case r: ::[FieldDescriptor] =>
              c.error(pathStr, r).                                    // Find errors.
                map(Left(_)).                                         // Propagate errors up.
                getOrElse(Right(ProtoAccessorImpl[A, B](r)))  // Or return a success.
            case Nil =>
              // This is here to appease the compiler.  Since `(fd :: fds).reverse` returns List instead
              // of ::, the compiler doesn't know that the list is non-empty.  Since List is sealed and
              // has 2 subclasses (Nil and ::), and only :: represents a non-empty list, Nil cannot occur.
              Left("Scala language bug: (fd :: fds).reverse should be a :: and never Nil.")
          }
        } getOrElse {
          Left(s"""No field "$h" found in ${d.getFullName}.""")
        }
      case h :: t =>
        Option(d.findFieldByName(h)) match {
          case Some(fd) if fd.getJavaType == MESSAGE => buildProtoAccessor(pathStr, t, fd :: fds, fd.getMessageType)
          case Some(fd) => Left(s"""${fd.getFullName} is not a message type but remaining path (${t.mkString(".")}) exists. Path: "$pathStr".""")
          case None => Left(s"""No field "$h" found in ${d.getFullName}. Path: "$pathStr".""")
        }
    }
  }

  /**
    * Cleans and splits the path string into consituent elements.
    * @param path the path string to split into
    * @return
    */
  private[pops] def splitPath(path: String): List[String] =
    // Can't use WrappedString method `nonEmpty` because of multiple implicit issue.  Use length > 0.
    path.split("""\.""").foldRight(List.empty[String]){(s, l) =>
      val t = s.trim
      if (t.length > 0) t :: l else l
    }

  /**
    * The sole concrete class extending [[ProtoAccessor]].
    *
    * '''Note''': since PB's `FieldDescriptor` is not `Serializable`, this class extends
    * `Serialiable`, overrides `readObject` and `writeObject`, requires a zero-arg
    * constructor, and requires all values to be changed to variables.  As a consequence,
    * member variables are made as private as possible.  This isn't a huge deal since this
    * class is made private to the [[ProtoAccessor]] companion object.  [[ProtoAccessor]]
    * only returns the trait in its factory methods and doesn't expose this concrete
    * implementation.
    * @param fds list of `FieldDescriptor`'s in the appropriate order.
    * @param ops an instance of [[ProtoOps]] to help with accessor function creation.
    * @param caster a caster that takes the untyped value returned by the PB reflection
    *               APIs and provides a typesafe value.
    * @tparam A type of `GeneratedMessage` from which data should be extracted.
    * @tparam B type of data to be extracted from `A`.
    */
  private[this] final case class ProtoAccessorImpl[A <: GeneratedMessage, B](
      private var fds: ::[FieldDescriptor])(implicit
      private var ops: ProtoOps[A],
      private var caster: Caster[A, B])
  extends ProtoAccessor[A, B] {

    /**
      * This horrible monstrosity is a result of `FieldDescriptor` not implementing Serializable.
      * @return
      */
    protected[this] def this() = this(null)(null, null)

    def apply(a: A): B = {
      // These casts are "OK" they were already vetted prior to the construction of `this`.
      @tailrec def h(fds: ::[FieldDescriptor], m: GeneratedMessage): B = fds match {
        case f :: (t : ::[FieldDescriptor]) => h(t, m.getField(f).asInstanceOf[GeneratedMessage])
        case f :: Nil                       => caster.cast(m.hasField(f), m.getField(f))
      }
      h(fds, a)
    }

    /**
      * Required because `FieldDescriptor` is not `Serializable`. Transform the path string
      * back into a list-based path representation of `FieldDescriptor` using the same process
      * that was used when originally creating [[ProtoAccessorImpl]].
      * @param in input
      */
    private def readObject(in: ObjectInputStream): Unit = {
      val path = in.readUTF()
      ops = in.readObject().asInstanceOf[ProtoOps[A]]
      caster = in.readObject().asInstanceOf[Caster[A, B]]
      ProtoAccessor.either[A, B](path) match {
        case Right(a: ProtoAccessorImpl[A, B]) => fds = a.fds
        case Left(m)                           => throw new IOException(m)
        case d                                 => throw new IOException(s"Unexpectedly found $d")
      }
    }

    /**
      * Required because `FieldDescriptor` is not `Serializable`.  Therefore, we need to
      * transform the list of `FieldDescriptor`s back to a path string.
      * @param out output stream
      */
    private def writeObject(out: ObjectOutputStream): Unit = {
      val path = fds.map(fd => fd.getName).mkString(".")
      out.writeUTF(path)
      out.writeObject(ops)
      out.writeObject(caster)
      out.flush()
    }
  }
}

package deaktator.proto.fn

import com.eharmony.aloha.score.Scores.Score
import com.eharmony.aloha.score.Scores.Score.BaseScore.ScoreType
import com.eharmony.aloha.score.Scores.Score._
import com.google.protobuf.GeneratedMessage
import deaktator.proto.SerializabilityTest
import org.scalatest.{FlatSpec, Matchers}


/**
  * Created by ryan on 4/24/16.
  */
class ProtoAccessorTest extends FlatSpec
                           with Matchers
                           with SerializabilityTest {

  import ProtoAccessorTest._

  "ProtoAccessor implicit factory method (Either version)" should "succeed with Option[String] accessor for some optional path elements." in {
    eitherSuccess(MId, Option(Name))("name")
  }

  it should "succeed with Option[Long] accessor for all required path elements." in {
    eitherSuccess(MId, Option(Id))("id")
  }

  it should "succeed with Option[Long] accessor for some optional path elements." in {
    eitherSuccess(TestError, Option(Id))("model.id")
  }

  it should "succeed with Long accessor for all required path elements." in {
    eitherSuccess(MId, Id)("id")
  }

  it should "succeed with Option[Enum] accessor for all required path elements." in {
    eitherSuccess(TestIntScore, Option(TestScoreType))("type")
  }

  it should "succeed with Option[Enum] accessor for some optional path elements." in {
    eitherSuccess(TestScoreWithInt, Option(TestScoreType))("score.type")
  }

  it should "succeed with Enum accessor for all required path elements." in {
    eitherSuccess(TestIntScore, TestScoreType)("type")
  }

  it should "succeed with Option[GeneratedMessage] accessor for all required path elements." in {
    eitherSuccess(TestError, Option(MId))("model")
  }

  it should "succeed with Option[GeneratedMessage] accessor for some optional path elements." in {
    eitherSuccess(TestScoreWithError, Option(MId))("error.model")
  }

  it should "succeed with GeneratedMessage accessor for all required path elements." in {
    eitherSuccess(TestError, MId)("model")
  }

  it should "succeed for a sequence of strings with a type ascription." in {
    val pas = Seq("id", "id") : Seq[Either[String, ProtoAccessor[ModelId, Long]]]
    val expectedSize = pas.size
    val fs: Seq[ModelId => Long] = pas.collect { case Right(f)  => f }
    pas.size should be (expectedSize)
    fs.size should be (expectedSize)
    fs.map(_.apply(MId)) should be (Seq.fill(expectedSize)(Id))
  }

  it should "succeed for a sequence of strings with an explicit variable type." in {
    val pas: Seq[Either[String, ProtoAccessor[ModelId, Long]]] = Seq("id", "id")
    val expectedSize = pas.size
    val fs: Seq[ModelId => Long] = pas.collect { case Right(f)  => f }
    pas.size should be (expectedSize)
    fs.size should be (expectedSize)
    fs.map(_.apply(MId)) should be (Seq.fill(expectedSize)(Id))
  }

  it should "fail when a non Option accessor is requested and the path include optional elements." in {
    val a: Either[String, ProtoAccessor[Score, Long]] = "error.model.id"
    a should be (Left("com.eharmony.aloha.score.Score.error is not a required field. ProtoAccessor output type should be an Option.  Path: \"error.model.id\"."))
  }

  it should "fail when path is empty." in {
    val a: Either[String, ProtoAccessor[Score, Long]] = ""
    a should be (Left("""ProtoAccessor path is empty. Provided: """""))
  }

  it should "fail when no path elements are present." in {
    val a: Either[String, ProtoAccessor[Score, Long]] = ". . "
    a should be (Left("""ProtoAccessor path is empty. Provided: ". . """"))
  }

  it should "fail when bad non-existent path elements are provided." in {
    val a: Either[String, ProtoAccessor[Score, Long]] = "error.y.id"
    a should be (Left("""No field "y" found in com.eharmony.aloha.score.Score.ScoreError. Path: "error.y.id"."""))
  }

  it should "fail additional path elements are provided when non GeneratedMessage is encountered." in {
    val a: Either[String, ProtoAccessor[Score, Option[Long]]] = "error.model.id.z"
    a should be (Left("""com.eharmony.aloha.score.Score.ModelId.id is not a message type but remaining path (z) exists. Path: "error.model.id.z"."""))
  }

  it should "fail when the type found in the Protocol Buffer doesn't match the specified type." in {
    val a: Either[String, ProtoAccessor[Score, Option[Int]]] = "error.model.id"
    a should be (Left("com.eharmony.aloha.score.Score.ModelId.id is wrong type.  Found LONG, expected one of {INT}.  Path: \"error.model.id\"."))
  }

  it should "be serializable" in {
    val a: Either[String, ProtoAccessor[Score, Option[Long]]] = "error.model.id"
    val f = a.right.get
    val f1 = serializeRoundTrip(f)
    f1(TestScoreWithError) should be (Option(Id))
  }

  "ProtoAccessor implicit factory method (Option version)" should "succeed with Option[String] accessor for some optional path elements." in {
    optionSuccess(MId, Option(Name))("name")
  }

  it should "succeed with Option[Long] accessor for all required path elements." in {
    optionSuccess(MId, Option(Id))("id")
  }

  it should "succeed with Option[Long] accessor for some optional path elements." in {
    optionSuccess(TestError, Option(Id))("model.id")
  }

  it should "succeed with Long accessor for all required path elements." in {
    optionSuccess(MId, Id)("id")
  }

  it should "succeed with Option[Enum] accessor for all required path elements." in {
    optionSuccess(TestIntScore, Option(TestScoreType))("type")
  }

  it should "succeed with Option[Enum] accessor for some optional path elements." in {
    optionSuccess(TestScoreWithInt, Option(TestScoreType))("score.type")
  }

  it should "succeed with Enum accessor for all required path elements." in {
    optionSuccess(TestIntScore, TestScoreType)("type")
  }

  it should "succeed with Option[GeneratedMessage] accessor for all required path elements." in {
    optionSuccess(TestError, Option(MId))("model")
  }

  it should "succeed with Option[GeneratedMessage] accessor for some optional path elements." in {
    optionSuccess(TestScoreWithError, Option(MId))("error.model")
  }

  it should "succeed with GeneratedMessage accessor for all required path elements." in {
    optionSuccess(TestError, MId)("model")
  }

  it should "succeed for a sequence of strings with a type ascription." in {
    val pas = Seq("id", "id") : Seq[Option[ProtoAccessor[ModelId, Long]]]
    val expectedSize = pas.size
    val fs: Seq[ModelId => Long] = pas.flatten
    pas.size should be (expectedSize)
    fs.size should be (expectedSize)
    fs.map(_.apply(MId)) should be (Seq.fill(expectedSize)(Id))
  }

  it should "succeed for a sequence of strings with an explicit variable type." in {
    val pas: Seq[Option[ProtoAccessor[ModelId, Long]]] = Seq("id", "id")
    val expectedSize = pas.size
    val fs: Seq[ModelId => Long] = pas.flatten
    pas.size should be (expectedSize)
    fs.size should be (expectedSize)
    fs.map(_.apply(MId)) should be (Seq.fill(expectedSize)(Id))
  }

  def eitherSuccess[A <: GeneratedMessage, B](a: A, expected: B)(acc: Either[String, ProtoAccessor[A, B]]) = {
    acc.isRight should be (true)
    val f = acc.right.get
    f(a) should be (expected)
  }

  def optionSuccess[A <: GeneratedMessage, B](a: A, expected: B)(acc: Option[ProtoAccessor[A, B]]) = {
    acc.nonEmpty should be (true)
    val f = acc.get
    f(a) should be (expected)
  }
}

object ProtoAccessorTest {
  val Id = 123L
  val IntScoreValue = 5
  val Name = "My name"
  val MId =  ModelId.newBuilder.setId(Id).setName(Name).build
  val TestError =
    ScoreError.newBuilder.
      setModel(MId).
      setMissingFeatures(MissingRequiredFields.getDefaultInstance).
      build

  val TestScoreWithError = Score.newBuilder.setError(TestError).build

  val TestScoreType = ScoreType.INT

  val TestIntScore =
    BaseScore.newBuilder.
      setModel(MId).
      setType(TestScoreType).
      setExtension(IntScore.impl, IntScore.newBuilder.setScore(IntScoreValue).build).
      build

  val TestScoreWithInt = Score.newBuilder.setScore(TestIntScore).build

}

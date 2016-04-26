package deaktator.proto

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

import com.eharmony.aloha.score.Scores.Score
import com.eharmony.aloha.score.Scores.Score.{ModelId, ScoreError}
import com.google.protobuf.GeneratedMessage
import deaktator.proto.msgs.ProtoOps
import org.apache.commons.codec.binary.Base64
import org.scalatest._

import scala.collection.JavaConversions.collectionAsScalaIterable


/**
  * Created by ryan on 4/11/16.
  */
class ProtoTest extends FlatSpec with Matchers {
  import ProtoTest._

  "Runtime ProtoOps instances" should "be serializable" in {
    checkSerializable(ProtoOps.runtime(classOf[Score]))
  }

  "Macro ProtoOps instances" should "operate the same as runtime ProtoOps instances" in {
    val macroOps = ProtoOps[Score]
    val runtimeOps = ProtoOps.runtime(classOf[Score])
    val b64 = encoded(someNonDefaultUserProto)

    val m = macroOps.parseFromB64(b64)
    val r = runtimeOps.parseFromB64(b64)

    m should be (r)
  }

  they should "be instantiable explicitly" in {
    Proto[Score].isInstanceOf[ProtoOps[Score]] should be (true)
  }

  they should "be serializable" in {
    checkSerializable(ProtoOps[Score])
  }

  they should "be able to deserialize Protos" in {
    assertSomeNonDefaultProtoIsCorrect(Proto[Score].parseFromB64(encoded(someNonDefaultUserProto)))
  }

  they should "be able to be generated and passed around implicitly" in {
    assertSomeNonDefaultProtoIsCorrect(new Converter[Score].decodeB64(encoded(someNonDefaultUserProto)))
  }

  they should "be at least twice as fast than runtime-based ProtoOps instances" in {
    val b64 = encoded(someNonDefaultUserProto)
    var i = 0
    var sum = 0L

    val n = 10000
    val runtimeProtoOps = ProtoOps.runtime[Score](classOf[Score])

    val (sR, tR) = time {
      while(i < n) {
        sum += runtimeProtoOps.parseFromB64(b64).getError.getModel.getId
        i += 1
      }
      sum
    }

    i = 0
    sum = 0

    val (sM, tM) = time {
      while(i < n) {
        sum += Proto[Score].parseFromB64(b64).getError.getModel.getId
        i += 1
      }
      sum
    }

    sM should be (n)
    sR should be (n)
    tM should be < (tR / 2)
  }

  def checkSerializable(ops: ProtoOps[Score]): Unit = {
    val baos = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(baos)
    oos.writeObject(ops)
    oos.close()
    val ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray))
    val deser = ois.readObject().asInstanceOf[ProtoOps[Score]]
    val s: Score = deser.parseFromB64(encoded(someNonDefaultUserProto))
    ois.close()
    assertSomeNonDefaultProtoIsCorrect(s)
  }

  def assertSomeNonDefaultProtoIsCorrect(p: Score): Unit = {
    p.getError.getModel.getId should be (1)
    p.getError.getModel.getName should be ("model")
    p.getError.getMessagesList.toList should be (List("fail"))
  }
}

object ProtoTest {
  class Converter[A <: GeneratedMessage](implicit ops: ProtoOps[A]) {
    def decodeB64(s: String): A = ops.parseFromB64(s)
  }

  def time[A](a: => A) = {
    val t1 = System.nanoTime()
    val r = a
    val t2 = System.nanoTime()
    (r, (1.0e-9*(t2 - t1)).toFloat)
  }

  def someNonDefaultUserProto: Score = {
    val m = ModelId.newBuilder.setId(1).setName("model")
    Score.newBuilder().setError(ScoreError.newBuilder.setModel(m).addMessages("fail")).build()
  }

  def encoded[A <: GeneratedMessage](a: A) = new String(Base64.encodeBase64(a.toByteArray))
}

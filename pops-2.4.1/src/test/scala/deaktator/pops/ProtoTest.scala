package deaktator.pops

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

import com.eharmony.aloha.score.Scores.Score
import com.eharmony.aloha.score.Scores.Score.{ModelId, ScoreError}
import com.google.protobuf.GeneratedMessage
import deaktator.pops.msgs.ProtoOps
import org.scalatest._

import scala.collection.JavaConversions.collectionAsScalaIterable


/**
  * @author deaktator
  */
class ProtoTest extends FlatSpec with Matchers {
  import ProtoTest._

  "Runtime ProtoOps instances" should "be serializable" in {
    checkSerializable(ProtoOps.runtime(classOf[Score]))
  }

  "Macro ProtoOps instances" should "operate the same as runtime ProtoOps instances" in {
    val macroOps = ProtoOps[Score]
    val runtimeOps = ProtoOps.runtime(classOf[Score])

    val bytes = someNonDefaultProto.toByteArray

    val m = macroOps.parseFrom(bytes)
    val r = runtimeOps.parseFrom(bytes)

    m should be (r)
  }

  they should "be instantiable explicitly" in {
    Proto[Score].isInstanceOf[ProtoOps[Score]] should be (true)
  }

  they should "be serializable" in {
    checkSerializable(ProtoOps[Score])
  }

  they should "be able to deserialize Protos" in {
    assertSomeNonDefaultProtoIsCorrect(Proto[Score].parseFrom(someNonDefaultProto.toByteArray))
  }

  they should "be able to be generated and passed around implicitly" in {
    assertSomeNonDefaultProtoIsCorrect(new Converter[Score].decode(someNonDefaultProto.toByteArray))
  }

  they should "be at least as fast than runtime-based ProtoOps instances" in {
    val bytes = someNonDefaultProto.toByteArray
    var i = 0
    var sum = 0L

    val n = 10000
    val runtimeProtoOps = ProtoOps.runtime[Score](classOf[Score])

    val (sR, tR) = time {
      while(i < n) {
        sum += runtimeProtoOps.parseFrom(bytes).getError.getModel.getId
        i += 1
      }
      sum
    }

    i = 0
    sum = 0

    val (sM, tM) = time {
      while(i < n) {
        sum += runtimeProtoOps.parseFrom(bytes).getError.getModel.getId
        i += 1
      }
      sum
    }

    sM should be (n)
    sR should be (n)
    tM should be < (tR)
  }

  def checkSerializable(ops: ProtoOps[Score]): Unit = {
    val baos = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(baos)
    oos.writeObject(ops)
    oos.close()
    val ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray))
    val deser = ois.readObject().asInstanceOf[ProtoOps[Score]]
    val s: Score = deser.parseFrom(someNonDefaultProto.toByteArray)
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
    def decode(a: Array[Byte]): A = ops.parseFrom(a)
  }

  def time[A](a: => A) = {
    val t1 = System.nanoTime()
    val r = a
    val t2 = System.nanoTime()
    (r, (1.0e-9*(t2 - t1)).toFloat)
  }

  def someNonDefaultProto: Score = {
    val m = ModelId.newBuilder.setId(1).setName("model")
    Score.newBuilder().setError(ScoreError.newBuilder.setModel(m).addMessages("fail")).build()
  }
}

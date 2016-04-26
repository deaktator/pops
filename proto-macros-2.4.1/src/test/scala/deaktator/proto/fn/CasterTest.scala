package deaktator.proto.fn

import com.eharmony.aloha.score.Scores.Score
import deaktator.proto.SerializabilityTest
import org.scalatest.{Matchers, FlatSpec}

/**
  * Created by ryan on 4/25/16.
  */
class CasterTest extends FlatSpec
                    with Matchers
                    with SerializabilityTest {

  "A Caster" should "be serializable." in {
    val expected = 5L
    val caster = Caster[Score, Long]
    val c = serializeRoundTrip(caster)
    c.cast(hasField = false, java.lang.Long.valueOf(expected).asInstanceOf[Any]) should be (expected)
  }
}

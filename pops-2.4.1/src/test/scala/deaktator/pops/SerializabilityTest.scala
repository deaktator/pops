package deaktator.pops

import java.io.{ObjectInputStream, ByteArrayInputStream, ObjectOutputStream, ByteArrayOutputStream}

/**
  * Created by ryan on 4/25/16.
  */
trait SerializabilityTest {
  def serializeRoundTrip[A](a: A): A = {
    val baos = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(baos)
    oos.writeObject(a)
    val bais = new ByteArrayInputStream(baos.toByteArray)
    oos.close()
    val ois = new ObjectInputStream(bais)
    val newA: A = ois.readObject().asInstanceOf[A]
    ois.close()
    newA
  }
}

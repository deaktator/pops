package deaktator.proto

import com.google.protobuf.GeneratedMessage

/**
  * Created by ryan on 4/12/16.
  */
object Proto {
  def apply[A <: GeneratedMessage](implicit ops: ProtoOps[A]): ProtoOps[A] = ops
}

package example

import chisel3._
import chisel3.util._

class MyFloat(eBits: Int = 1, mBits: Int = 1) extends Bundle {
  val sign = Bool()
  val exponent = UInt(eBits.W)
  val mantissa = UInt(mBits.W)
  override def cloneType =
    new MyFloat(eBits, mBits).asInstanceOf[this.type]
}

class Foo(eBits: Int = 1, mBits: Int = 1) extends Module {
  val btype = Wire(new MyFloat(eBits, mBits))
  btype := DontCare
  val io = IO(new Bundle {
    val a = Input(chiselTypeOf(btype))
    val x = Output(new MyFloat(eBits, mBits))
    val y = Output(chiselTypeOf(btype))
  })
  val r = Reg(new MyFloat(eBits, mBits))

  // x (bulk connection)
  r <> io.a
  io.x <> r

  // y (port-to-port)
  io.y.sign := r.sign
  io.y.exponent := r.exponent & "h_f".U
  io.y.mantissa := r.mantissa
}

object Elaborate extends App {
  val eBits = 8
  val mBits = 23
  chisel3.Driver.execute(args, () => new Foo(eBits, mBits))
}

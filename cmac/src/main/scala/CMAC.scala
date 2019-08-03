package example

import chisel3._
import chisel3.util._

class CMAC(stages: Int = 8, length: Int = 8, aBits: Int = 8, bBits: Int = 4) extends Module {
  val mBits = aBits + bBits
  val io = IO(new Bundle {
    val a = Flipped(ValidIO(Vec(length, SInt(aBits.W))))
    val b = Flipped(ValidIO(Vec(length, SInt(bBits.W))))
    val y = ValidIO(SInt(32.W))
  })
  val m = VecInit(Seq.fill(length)(0.asSInt((mBits).W)))
  for (i <- 0 until length) {
    m(i) := io.a.bits(i) * io.b.bits(i)
  }
  val sum = m.reduce(_+&_)
  io.y.valid := ShiftRegister(io.a.valid & io.b.valid, stages)
  io.y.bits := ShiftRegister(sum, stages)
}

object Elaborate extends App {
  chisel3.Driver.execute(args, () => new CMAC)
}

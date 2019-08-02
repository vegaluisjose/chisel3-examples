package example

import chisel3._
import chisel3.util._

class CMAC(stages: Int = 8, length: Int = 8, dataBits: Int = 8) extends Module {
  val io = IO(new Bundle {
    val a = Flipped(ValidIO(Vec(length, SInt(dataBits.W))))
    val b = Flipped(ValidIO(Vec(length, SInt(dataBits.W))))
    val y = ValidIO(SInt(32.W))
  })
  val m = VecInit(Seq.fill(length)(0.asSInt((2*dataBits).W)))
  for (i <- 0 until length) {
    m(i) := io.a.bits(i) * io.b.bits(i)
  }
  val sum = m.reduce(_+&_)
  io.y.valid := ShiftRegister(io.a.valid & io.b.valid, stages, io.a.valid & io.b.valid)
  io.y.bits := ShiftRegister(sum, stages, io.a.valid & io.b.valid)
}

object Elaborate extends App {
  chisel3.Driver.execute(args, () => new CMAC)
}
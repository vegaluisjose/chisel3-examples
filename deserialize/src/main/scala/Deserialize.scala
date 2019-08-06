package example

import chisel3._
import chisel3.util._

class Deserialize(inBits: Int = 64, outBits: Int = 8, outSize: Int = 16) extends Module {
  require((outBits * outSize) % inBits == 0)
  val inSize = (outBits * outSize) / inBits
  val io = IO(new Bundle {
    val in = Input(ValidIO(UInt(inBits.W)))
    val out = Output(Vec(outSize, UInt(outBits.W)))
  })
  val r = Reg(Vec(inSize, UInt(inBits.W)))
  when (io.in.valid) {
    r(0) := io.in.bits
    for (i <- 1 until inSize) {
      r(i) := r(i-1)
    }
  }
  io.out := r.asUInt.asTypeOf(io.out)
}

object Elaborate extends App {
  chisel3.Driver.execute(args, () => new Deserialize)
}
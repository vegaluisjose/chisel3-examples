package example

import chisel3._
import chisel3.util._

class Adder(dataBits: Int = 8) extends Module {
  val io = IO(new Bundle {
    val a = Input(UInt(dataBits.W))
    val b = Input(UInt(dataBits.W))
    val y = Output(UInt(dataBits.W))
  })
  io.y := io.a + io.b
}

object Elaborate extends App {
  chisel3.Driver.execute(args, () => new Adder)
}

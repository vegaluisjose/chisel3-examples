package example.test

import chisel3._
import chisel3.util._

import example._

class DataGen(dataBits: Int = 8) extends Module {
  val io = IO(new Bundle {
    val a = Output(UInt(dataBits.W))
    val b = Output(UInt(dataBits.W))
    val y = Input(UInt(dataBits.W))
  })

  val (cnt, _) = Counter(true.B, 256)

  io.a := cnt
  io.b := cnt % 2.U

  when(true.B) {
    printf("a:%x b:%x y:%x\n", io.a, io.b, io.y)
  }
}

class Test extends Module {
  val io = IO(new Bundle {})
  val gen = Module(new DataGen)
  val add = Module(new Adder)
  add.io.a := gen.io.a
  add.io.b := gen.io.b
  gen.io.y := add.io.y
}

object Elaborate extends App {
  chisel3.Driver.execute(args, () => new Test)
}

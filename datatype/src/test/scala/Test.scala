package example.test

import chisel3._
import chisel3.util._
import chisel3.experimental.{RawModule, withClockAndReset}

import example._

class DataGen extends Module {
  val io = IO(new Bundle {
    val a = Output(Bool())
    val b = Output(Bool())
    val y = Input(Bool())
  })

  val (cnt, _) = Counter(true.B, 1024)

  io.a := cnt === 5.U
  io.b := cnt >= 3.U
  when(io.y) {printf("\n\nY is true\n\n")}
}

class Test extends RawModule {
  val clock = IO(Input(Clock()))
  val reset = IO(Input(Bool()))

  val eBits = 8
  val mBits = 23
  val gen = withClockAndReset(clock, reset) { Module(new DataGen) }
  val foo = withClockAndReset(clock, reset) { Module(new Foo(eBits, mBits)) }

  foo.io.a := gen.io.a
  foo.io.b := gen.io.b
  gen.io.y := foo.io.y
}

//class Test extends Module {
//  val io = IO(new Bundle{val i = Input(Bool())})
//
//  val eBits = 8
//  val mBits = 23
//  val foo = Module(new Foo(eBits, mBits))
//
//  foo.io.a.sign := true.B
//  foo.io.a.exponent := "h_A".U
//  foo.io.a.mantissa := "h_F".U
//
//  when(true.B) { printf("exp:%x\n", foo.io.x.exponent) }
//}

object Elaborate extends App {
  chisel3.Driver.execute(args, () => new Test)
}

package example.test

import chisel3._
import chisel3.util._
import chisel3.experimental.{RawModule, withClockAndReset}

import example._

class DataGen(eBits: Int = 1, mBits: Int = 1) extends Module {
  val io = IO(new Bundle {
    val a = Output(new MyFloat(eBits, mBits))
    val x = Input(new MyFloat(eBits, mBits))
    val y = Input(new MyFloat(eBits, mBits))
  })

  val (cnt, _) = Counter(true.B, 256)

  io.a.sign := true.B
  io.a.exponent := cnt
  io.a.mantissa := 0.U

  when(true.B) {
    printf("\n[x] sign:%b exponent:%x mantissa:%x\n", io.x.sign, io.x.exponent, io.x.mantissa)
    printf("[y] sign:%b exponent:%x mantissa:%x\n", io.y.sign, io.y.exponent, io.y.mantissa)
  }
}

class Test extends RawModule {
  val clock = IO(Input(Clock()))
  val reset = IO(Input(Bool()))

  val eBits = 8
  val mBits = 23
  val gen = withClockAndReset(clock, reset) { Module(new DataGen(eBits, mBits)) }
  val foo = withClockAndReset(clock, reset) { Module(new Foo(eBits, mBits)) }

  foo.io.a <> gen.io.a
  gen.io.x <> foo.io.x
  gen.io.y <> foo.io.y
}

object Elaborate extends App {
  chisel3.Driver.execute(args, () => new Test)
}

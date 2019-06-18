package example.test

import chisel3._
import chisel3.util._

import example._

class DataGen(eBits: Int = 1, mBits: Int = 1) extends Module {
  val io = IO(new Bundle {
    val in = Flipped(ValidIO(new MyFloat(eBits, mBits)))
    val out = ValidIO(new MyFloat(eBits, mBits))
  })

  val (cycles, _) = Counter(true.B, 256)

  io.out.valid := cycles === 12.U
  when (cycles === 12.U) {
    io.out.bits.sign := true.B
    io.out.bits.exponent := "h_AA".U
    io.out.bits.mantissa := "h_BEEF".U
  } .otherwise {
    io.out.bits.sign := false.B
    io.out.bits.exponent := 0.U
    io.out.bits.mantissa := 0.U
  }

  when(io.out.valid) {
    printf("[PIPELINE-I] cycles:%x sign:%b exponent:%x mantissa:%x\n", cycles, io.out.bits.sign, io.out.bits.exponent, io.out.bits.mantissa)
  }

  when(io.in.valid) {
    printf("[PIPELINE-O] cycles:%x sign:%b exponent:%x mantissa:%x\n", cycles, io.in.bits.sign, io.in.bits.exponent, io.in.bits.mantissa)
  }
}

class Test extends Module {
  val io = IO(new Bundle {})
  val depth = 8
  val eBits = 8
  val mBits = 23
  val gen = Module(new DataGen(eBits, mBits))
  val foo = Module(new Foo(depth, eBits, mBits))
  foo.io.in <> gen.io.out
  gen.io.in <> foo.io.out
}

object Elaborate extends App {
  chisel3.Driver.execute(args, () => new Test)
}

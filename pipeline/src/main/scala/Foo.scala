package example

import chisel3._
import chisel3.util._

class MyFloat(eBits: Int = 1, mBits: Int = 1) extends Bundle {
  val sign = Input(Bool())
  val exponent = Input(UInt(eBits.W))
  val mantissa = Input(UInt(mBits.W))
  override def cloneType =
    new MyFloat(eBits, mBits).asInstanceOf[this.type]
}

class Bar(eBits: Int = 1, mBits: Int = 1) extends Module {
  val io = IO(new Bundle {
    val in = Flipped(ValidIO(new MyFloat(eBits, mBits)))
    val out = ValidIO(new MyFloat(eBits, mBits))
  })
  io.out.valid := RegNext(io.in.valid)
  io.out.bits <> RegNext(io.in.bits)
}

class Foo(depth: Int = 1, eBits: Int = 1, mBits: Int = 1) extends Module {
  val io = IO(new Bundle {
    val in = Flipped(ValidIO(new MyFloat(eBits, mBits)))
    val out = ValidIO(new MyFloat(eBits, mBits))
  })
  val b = Seq.tabulate(depth)(i => Module(new Bar(eBits, mBits)))
  val pWire = Seq.tabulate(depth+1)(i => Wire(ValidIO(new MyFloat(eBits, mBits))))

  // input
  pWire(0) <> io.in

  // pipeline
  for (i <- 0 until depth) {
    b(i).io.in <> pWire(i)
    pWire(i+1) <> b(i).io.out
  }

  // output
  io.out <> pWire(depth)
}

object Elaborate extends App {
  val depth = 8
  val eBits = 8
  val mBits = 23
  chisel3.Driver.execute(args, () => new Foo(depth, eBits, mBits))
}

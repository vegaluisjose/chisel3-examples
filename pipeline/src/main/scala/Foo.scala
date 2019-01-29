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
  val bar = Seq.tabulate(depth)(i => Module(new Bar(eBits, mBits)))
  val wire = Seq.tabulate(depth+1)(i => Wire(ValidIO(new MyFloat(eBits, mBits))))

  // input
  wire(0) <> io.in

  // pipeline
  //
  // Conventional way
  // for (i <- 0 until depth) {
  //   bar(i).io.in <> wire(i)
  //   wire(i+1) <> bar(i).io.out
  // }

  // Another
  bar.zipWithIndex.foreach { case(bar, i) =>
    bar.io.in <> wire(i)
    wire(i + 1) <> bar.io.out
  }

  // output
  io.out <> wire(depth)
}

object Elaborate extends App {
  val depth = 8
  val eBits = 8
  val mBits = 23
  chisel3.Driver.execute(args, () => new Foo(depth, eBits, mBits))
}

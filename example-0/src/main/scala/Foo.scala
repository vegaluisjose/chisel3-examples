package example

import chisel3._
import chisel3.util._

class Foo(width: Int = 1, depth: Int = 1) extends Module {
  val io = IO(new Bundle {
    val in = Input(Bool())
  })
}

object Foo extends App {
  chisel3.Driver.execute(args, () => new Foo(width = 1, depth = 16))
}

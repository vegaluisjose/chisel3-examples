package example.test

import chisel3._
import chisel3.util._
import chisel3.experimental.{RawModule, withClockAndReset}

import example._

class Test extends Module {
  val clock = IO(Input(Clock()))
  val reset = IO(Input(Bool()))

  val foo = withClockAndReset (clock, reset) { Module(new Foo) }

  foo.io.clock := clock
  foo.io.reset := reset
  foo.io.in := true.B
}

object Elaborate extends App {
  chisel3.Driver.execute(args, () => new Test)
}

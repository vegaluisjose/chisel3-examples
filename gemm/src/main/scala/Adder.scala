package example

import chisel3._
import chisel3.core.CompileOptions
import chisel3.util._
import chisel3.internal.sourceinfo.SourceInfo

object Vectorized {
  def vectorize2[A <: Data, B <: Data, C <: Data](x: Vec[A], y: Vec[B])(f: (A, B) => C): Vec[C] =
    Vec(x.zip(y).map { case (i, j) => f(i, j) })

  implicit def toVectorize[T <: Data with Num[T]](vec: Vec[T]) : Vectorized[T] =
    new Vectorized(vec)

  type MatrixT[T <: Data] = Vec[Vec[T]]

  def Matrix(row: Int, column: Int, dataBits: Int) =
    Vec(row, Vec(column, UInt(dataBits.W)))
}

import Vectorized._

class Vectorized[T <: Data with Num[T]](val vec: Vec[T]) extends Data with Num[Vec[T]] {
  def do_+(that: Vec[T])(implicit sourceInfo: SourceInfo, compileOptions: CompileOptions): Vec[T] =
    Vectorized.vectorize2(this.vec, that.vec)(_ + _)

  def do_/(that: Vec[T])(implicit sourceInfo: SourceInfo, compileOptions: CompileOptions): Vec[T] =
    Vectorized.vectorize2(this.vec, that.vec)(_ / _)

  def do_-(that: Vec[T])(implicit sourceInfo: SourceInfo,compileOptions: CompileOptions): Vec[T] =
    Vectorized.vectorize2(this.vec, that.vec)(_ - _)

  def do_%(that: Vec[T])(implicit sourceInfo: SourceInfo,compileOptions: CompileOptions): Vec[T] =
    Vectorized.vectorize2(this.vec, that.vec)(_ % _)

  def do_*(that: Vec[T])(implicit sourceInfo: SourceInfo,compileOptions: CompileOptions): Vec[T] =
    Vectorized.vectorize2(this.vec, that.vec)(_ * _)

  def do_>(that: Vec[T])(implicit sourceInfo: SourceInfo,compileOptions: CompileOptions): chisel3.core.Bool =
    ??? // Vectorized.vectorize2(this.vec, that.vec)(_ > _)

  def do_>=(that: Vec[T])(implicit sourceInfo: SourceInfo,compileOptions: CompileOptions): chisel3.core.Bool =
    ??? // Vectorized.vectorize2(this.vec, that.vec)(_ >= _)

  def do_<(that: Vec[T])(implicit sourceInfo: SourceInfo,compileOptions: CompileOptions): chisel3.core.Bool =
    ??? // Vectorized.vectorize2(this.vec, that.vec)(_ < _)

  def do_<=(that: Vec[T])(implicit sourceInfo: SourceInfo, compileOptions: CompileOptions): chisel3.core.Bool =
    ??? // Vectorized.vectorize2(this.vec, that.vec)(_ <= _)

  def do_abs(implicit sourceInfo: SourceInfo,compileOptions: CompileOptions): Vec[T] = ???

  def dot(that: Vec[T]): T =
    vectorize2(this.vec, that.vec)(_ * _).fold(0.U.asTypeOf(chiselTypeOf(vec.head)))(_ + _)

  override def allElements: Seq[chisel3.core.Element] = ???
  override def bind(target: chisel3.core.Binding,parentDirection: chisel3.core.SpecifiedDirection): Unit = ???
  override def cloneType: Vectorized.this.type = ???
  override def connectFromBits(that: chisel3.core.Bits)(implicit sourceInfo: SourceInfo, compileOptions: CompileOptions): Unit = ???
  def do_asUInt(implicit sourceInfo: SourceInfo, compileOptions: CompileOptions): chisel3.core.UInt = ???
  override def legacyConnect(that: chisel3.core.Data)(implicit sourceInfo: SourceInfo): Unit = ???
  def toPrintable: chisel3.core.Printable = ???
  override def typeEquivalent(that: chisel3.core.Data): Boolean = ???
  override def width: chisel3.internal.firrtl.Width = this.vec.width
}

class Adder(length: Int = 2, dataBits: Int = 8) extends Module {
  val io = IO(new Bundle {
    val a = Input(Vec(length, UInt(dataBits.W)))
    val b = Input(Vec(length, UInt(dataBits.W)))
    val y = Output(Vec(length, UInt(dataBits.W)))
  })
  io.y := io.a + io.b
}

class Dotter(length: Int = 2, dataBits: Int = 8) extends Module {
  val io = IO(new Bundle {
    val a = Input(Vec(length, UInt(dataBits.W)))
    val b = Input(Vec(length, UInt(dataBits.W)))
    val y = Output(UInt(dataBits.W))
  })
  io.y := io.a.dot(io.b)
}

class Tranpose(row: Int = 2, column: Int = 2, dataBits: Int = 8) extends Module {
  val io = IO(new Bundle {
    val in = Input(Matrix(row, column, dataBits))
    val out = Output(Matrix(column, row, dataBits))
  })

  for (i <- 0 until row) {
    for (j <- 0 until column) {
      io.out(j)(i) := io.in(i)(j)
    }
  }
}

class GEMM(row: Int = 2, column: Int = 2, dataBits: Int = 8) extends Module {
  val io = IO(new Bundle {
    val a = Input(Matrix(row, column, dataBits))
    val b = Input(Matrix(row, column, dataBits))
    val y = Output(Matrix(row, column, dataBits))
  })

  val trans = Module(new Tranpose(row, column, dataBits))
  trans.io.in := io.b

  for (i <- 0 until row) {
    for (j <- 0 until column) {
      io.y(j)(i) := io.a(i).dot(trans.io.out(j))
    }
  }
}

object Elaborate extends App {
  chisel3.Driver.execute(args, () => new GEMM(1, 1))
}

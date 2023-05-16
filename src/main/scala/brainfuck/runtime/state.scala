package brainfuck.runtime

import collection.mutable.Queue
import scala.io.StdIn.readLine

import brainfuck.unescape.unescape
import brainfuck.parse.*
import brainfuck.color.*

/** State of brainfuck runtime.
  */
class State[T <: Io](
    val size: Int
):
  var memory: Array[Int] = Array.fill(size)(0)
  var pointer: Int = 0
  var input_stream: Queue[Char] = Queue()

  private def inc(): Unit =
    memory(pointer) += 1
    memory(pointer) %= 128

  private def dec(): Unit =
    memory(pointer) += 127
    memory(pointer) %= 128

  private def incPointer(): Unit =
    pointer += 1
    pointer %= size

  private def decPointer(): Unit =
    pointer += size - 1
    pointer %= size

  private def outputChar[T <: Io](io: T): Unit =
    io.output(memory(pointer).toChar)

  private def inputChar[T <: Io](
      io: T,
      option: RuntimeOption
  ): RuntimeResult[Unit] =
    import RuntimeError.*
    while input_stream.isEmpty do
      val line = io.input()
      if option.escape_input then
        unescape(line) match
          case Right(s) => input_stream ++= s
          case Left(e)  => return Left(InvalidEscape(e))
      else input_stream ++= line
    memory(pointer) = input_stream.dequeue().toInt
    Right(())

  private def isZero(): Boolean =
    memory(pointer) == 0

  //

  private def printStep(entire: Block, pc: Int) =
    entire.printCurrent(pc)
    println()
    printMemory()
    println("===" * size)
    readLine()

  private def execBlock[T <: Io](
      block: Block,
      entire: Block,
      io: T,
      option: RuntimeOption
  ): Unit =
    import Op.*

    block.ops match
      case Nil => ()
      case (Inc, pc) :: rest =>
        inc()
        if option.run_step then printStep(entire, pc)
        execBlock(Block(rest), entire, io, option)
      case (Dec, pc) :: rest =>
        dec()
        if option.run_step then printStep(entire, pc)
        execBlock(Block(rest), entire, io, option)
      case (IncPointer, pc) :: rest =>
        incPointer()
        if option.run_step then printStep(entire, pc)
        execBlock(Block(rest), entire, io, option)
      case (DecPointer, pc) :: rest =>
        decPointer()
        if option.run_step then printStep(entire, pc)
        execBlock(Block(rest), entire, io, option)
      case (Output, pc) :: rest =>
        outputChar(io)
        if option.run_step then
          println()
          printStep(entire, pc)
        execBlock(Block(rest), entire, io, option)
      case (Input, pc) :: rest =>
        inputChar(io, option) match
          case Left(e) => io.err(e)
          case _ =>
            if option.run_step then printStep(entire, pc)
            execBlock(Block(rest), entire, io, option)
      case (Loop(loop), pc) :: rest =>
        while !isZero() do execBlock(loop, entire, io, option)
        execBlock(Block(rest), entire, io, option)
      case (Other(c), pc) :: rest =>
        execBlock(Block(rest), entire, io, option)

  /** Execute program.
    */
  def exec[T <: Io](program: String, io: T, option: RuntimeOption): Unit =
    mkBlock(program) match
      case Right(block) =>
        execBlock(block, block, io, option)
      case Left(e) => io.err(RuntimeError.FailedParse(e))

  /** Discard input stream.
    */
  def discardInput(): Unit =
    input_stream = Queue()

  /** Print current memory state.
    */
  def printMemory() =
    val pointer_color = RED
    val memory_color = CYAN
    for (m, i) <- memory.zipWithIndex do
      val color =
        if i == pointer then pointer_color else memory_color
      print(color)
      print(f"$m%02x ")
    println()
    for (m, i) <- memory.zipWithIndex do
      val color =
        if i == pointer then pointer_color else memory_color
      print(color)
      print(" ")
      if m >= 32 && m <= 126 then print(m.toChar)
      else print(" ")
      print(" ")
    println()
    print(" " * (pointer * 3))
    print(pointer_color)
    println("^^")
    print(DEFAULT)

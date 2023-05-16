package brainfuck.runtime

import scala.io.StdIn.readLine

/** Implementation of runtime IO.
  */
trait Io:
  def input(): String
  def output(c: Char): Unit
  def err(e: RuntimeError): Unit

/** Standard IO.
  */
class StdIo extends Io:
  override def input() = readLine()

  override def output(c: Char) = print(c)

  override def err(e: RuntimeError) =
    println(s"\u001b[31m$e\u001b[0m")

package brainfuck.repl

import brainfuck.runtime.RuntimeError
import brainfuck.runtime.State
import brainfuck.runtime.StdIo
import java.io.FileReader

/** Brainfuck REPL.
  *
  * @param size
  * @param option
  */
class Repl(
    size: Int,
    var option: ReplOption = ReplOption()
):
  private val io = StdIo()

  private var state = State(size)

  def clear(new_size: Int = state.size) =
    state = State(new_size)
    if option.print_state then printState()

  /** Set option to enable escaping in input.
    *
    * @param set
    */
  def setEscapeInput(set: Boolean) =
    option.runtime.escape_input = set

  /** Set option to discard input when REPL executes code.
    *
    * @param set
    */
  def setDiscardInput(set: Boolean) =
    option.discard_input = set

  /** Set option to print current state when REPL executes code.
    *
    * @param set
    */
  def setPrintState(set: Boolean) =
    option.print_state = set

  /** Set option to execute code step-by-step.
    *
    * @param set
    */
  def setRunStep(set: Boolean) =
    option.runtime.run_step = set

  /** Set option to clear state when REPL executes code.
    *
    * @param set
    */
  def setAutoClear(set: Boolean) =
    option.auto_clear = set

  /** Discard input stream.
    */
  def discardInput() =
    state.discardInput()

  /** Print current state.
    */
  def printState() =
    state.printMemory()

  /** Load external brainfuck source.
    *
    * @param path
    */
  def load(path: String) =
    try
      val source = scala.io.Source.fromFile(path)
      val program = source.mkString
      source.close()
      exec(program)
    catch case _ => io.err(RuntimeError.FileNotFound(path))

  /** Execute program.
    *
    * @param program
    */
  def exec(program: String) =
    state.exec(program, io, option.runtime)
    if option.discard_input then discardInput()
    println()
    if option.print_state then printState()
    if option.auto_clear then clear()

  /** Launch REPL.
    *
    * > clear <size> : clear memory
    *
    * > set <option> [<arg>] : set option
    *
    * > get <option> [<arg>] : get option value
    *
    * > discard_input : discard input stream
    *
    * > state : print current state
    *
    * > exit : exit REPL
    *
    * > load : load external source
    *
    * > exec <program> : execute program
    *
    * > <program> : execute program
    */
  def launch(): Unit =
    while true do
      print("> ")
      val command = scala.io.StdIn.readLine().strip()
      if !command.isEmpty() then
        val command_sep = command.split(" ")
        if command_sep.length >= 1 then
          command_sep(0) match
            case "clear" =>
              if command_sep.length == 1 then clear()
              else if command_sep.length == 2 then
                if command_sep(1).forall(_.isDigit) then
                  clear(command_sep(1).toInt)
                else println("usage: clear <size>")
              else println("usage: clear <size>")
            case "set" =>
              if command_sep.length == 1 then
                println("usage: set <option> [<arg>]")
              else
                command_sep(1) match
                  case "escape_input" =>
                    if command_sep.length == 3 then
                      if command_sep(2) == "true" then setEscapeInput(true)
                      else if command_sep(2) == "false" then
                        setEscapeInput(false)
                      else println("usage: set escape_input <bool>")
                    else println("usage: set escape_input <bool>")
                  case "discard" =>
                    if command_sep.length == 3 then
                      if command_sep(2) == "true" then setDiscardInput(true)
                      else if command_sep(2) == "false" then
                        setDiscardInput(false)
                      else println("usage: set discard <bool>")
                    else println("usage: set discard <bool>")
                  case "print_state" =>
                    if command_sep.length == 3 then
                      if command_sep(2) == "true" then setPrintState(true)
                      else if command_sep(2) == "false" then
                        setPrintState(false)
                      else println("usage: set print_state <bool>")
                    else println("usage: set print_state <bool>")
                  case "run_step" =>
                    if command_sep.length == 3 then
                      if command_sep(2) == "true" then setRunStep(true)
                      else if command_sep(2) == "false" then setRunStep(false)
                      else println("usage: set run_step <bool>")
                    else println("usage: set run_step <bool>")
                  case "auto_clear" =>
                    if command_sep.length == 3 then
                      if command_sep(2) == "true" then setAutoClear(true)
                      else if command_sep(2) == "false" then setAutoClear(false)
                      else println("usage: set auto_clear <bool>")
                    else println("usage: set run_step <bool>")
                  case s =>
                    println(f"unknown option: $s")
            case "get" =>
              if command_sep.length == 1 then println("usage: get <option>")
              else if command_sep.length == 2 then
                command_sep(1) match
                  case "escape_input" =>
                    println(option.runtime.escape_input)
                  case "discard" =>
                    println(option.discard_input)
                  case "print_state" =>
                    println(option.print_state)
                  case "run_step" =>
                    println(option.runtime.run_step)
                  case "auto_clear" =>
                    println(option.auto_clear)
                  case s =>
                    println(f"unknown option: $s")
              else println("usage: get <option>")
            case "discard_input" =>
              if command_sep.length == 1 then discardInput()
              else println("usage: discard_input")
            case "state" =>
              if command_sep.length == 1 then printState()
              else println("usage: state")
            case "exit" =>
              println("bye")
              return
            case "load" =>
              if command_sep.length == 2 then load(command_sep(1))
              else println("usage: load <path>")
            case "exec" =>
              exec(command.drop(4))
            case _ =>
              exec(command)

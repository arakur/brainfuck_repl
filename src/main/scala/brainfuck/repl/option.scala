package brainfuck.repl

import brainfuck.runtime.RuntimeOption

/** Options on Brainfuck REPL.
  *
  * @param runtime
  *   : Runtime option.
  * @param discard_input
  *   : If true, REPL discards input stream in runtime state.
  * @param print_state
  *   : If true, REPL prints current runtime state when bf code is executed.
  */
class ReplOption(
    var runtime: RuntimeOption = RuntimeOption(),
    var discard_input: Boolean = true,
    var print_state: Boolean = true,
    var auto_clear: Boolean = false
)

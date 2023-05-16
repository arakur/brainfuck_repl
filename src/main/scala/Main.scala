import brainfuck.repl.*

@main
def main: Unit =
  val size = 32
  val repl = Repl(size = size)
  repl.launch()

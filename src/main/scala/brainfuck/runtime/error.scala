package brainfuck.runtime

import brainfuck.unescape.UnescapeError
import brainfuck.parse.ParseError

/** Kinds of runtime error.
  */
enum RuntimeError:
  case InvalidEscape(e: UnescapeError)
  case FailedParse(e: ParseError)
  case FileNotFound(path: String)

  override def toString(): String =
    this match
      case InvalidEscape(e) =>
        s"Invalid Escape Error: $e"
      case FailedParse(e) =>
        s"Parse Error: $e"
      case FileNotFound(path) =>
        s"File not found: $path"

/** Result of runtime.
  */
type RuntimeResult[T] = Either[RuntimeError, T]

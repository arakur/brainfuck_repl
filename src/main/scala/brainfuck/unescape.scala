package brainfuck.unescape

private def isOctDigit(c: Char): Boolean =
  '0' <= c && c <= '7'

private def isHexDigit(c: Char): Boolean =
  '0' <= c && c <= '9' ||
    'a' <= c && c <= 'f' ||
    'A' <= c && c <= 'F'

//

/** An error occurring when unescape failed. */
enum UnescapeError:
  case InvalidAsciiCode(s: String)
  case EmptyEscapeSequence()
  case InvalidEscapeSequence(c: Char)

  override def toString(): String =
    this match
      case InvalidAsciiCode(s) =>
        s"Invalid ascii code $s"
      case EmptyEscapeSequence() =>
        "Empty escape sequence"
      case InvalidEscapeSequence(c) =>
        s"Invalid escape sequence $c"

/** Result of trying to unescape an input string. */
type UnescapeResult[T] = Either[UnescapeError, T]

/** Make an input string from a user be unescaped with the following sequences:
  *
  * \00 ~ \7F .. an ascii character specified with hexadecimal number 00 ~ 7F,
  *
  * \\ : an escape sequence for '\'.
  *
  * @param s
  * @return
  */
def unescape(s: String): UnescapeResult[String] =
  import UnescapeError.*
  def ue(s: List[Char]): UnescapeResult[List[Char]] =
    s match
      case Nil => Right(Nil)
      case '\\' :: esc =>
        esc match
          case Nil => Left(EmptyEscapeSequence())
          case d0 :: d1 :: next if isOctDigit(d0) && isHexDigit(d1) =>
            val d = Integer.parseInt(List(d0, d1).mkString, 16).toChar
            ue(next).map(d :: _)
          case d0 :: d1 :: next
              if isHexDigit(d1) || isOctDigit(d0) && !isHexDigit(d1) =>
            Left(InvalidAsciiCode(List(d0, d1).mkString))
          case '\\' :: next => ue(next).map('\\' :: _)
          case c :: next    => Left(InvalidEscapeSequence(c))
      case c :: next => ue(next).map(c :: _)
  ue(s.toList).map(_.mkString)

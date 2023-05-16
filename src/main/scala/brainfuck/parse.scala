package brainfuck.parse

import brainfuck.color.*

/** Error occurs in parsing brainfuck code.
  */
enum ParseError:
  case BracketNotClosed(begins: Int)
  case BracketNotOpened(ends: Int)

  override def toString(): String =
    this match
      case BracketNotClosed(begins) =>
        s"[ at $begins is not closed"
      case BracketNotOpened(ends) =>
        s"found ] at $ends but no matching ["

/** Result of parsing brainfuck code.
  */
type ParseResult[T] = Either[ParseError, T]

//

private[brainfuck] class Block(val ops: List[(Op, Int)]):
  def printCurrent(pc: Int): Unit =
    val color_pc = GREEN
    val color_default = DEFAULT
    for (op, i) <- ops do
      if i == pc then
        print(color_pc)
        op.printCurrent(pc)
        print("|")
        print(color_default)
      else op.printCurrent(pc)

private[brainfuck] enum Op:
  case Inc
  case Dec
  case IncPointer
  case DecPointer
  case Output
  case Input
  case Loop(block: Block)
  case Other(c: Char)

  def printCurrent(pc: Int): Unit =
    this match
      case Inc        => print("+")
      case Dec        => print("-")
      case IncPointer => print(">")
      case DecPointer => print("<")
      case Output     => print(".")
      case Input      => print(",")
      case Loop(block) =>
        print("[")
        block.printCurrent(pc)
        print("]")
      case Other(c) => print(c)

// parse brainfuck code.
private[brainfuck] def mkBlock(s: String): ParseResult[Block] =
  import ParseError.*
  def mk(
      s: List[Char],
      acc: List[(Op, Int)],
      count: Int,
      start_loop: Option[Int]
  ): ParseResult[(List[(Op, Int)], List[Char], Int)] =
    s match
      case Nil => Right(acc, List(), count)
      case '+' :: next =>
        mk(next, (Op.Inc, count) :: acc, count + 1, start_loop)
      case '-' :: next =>
        mk(next, (Op.Dec, count) :: acc, count + 1, start_loop)
      case '>' :: next =>
        mk(next, (Op.IncPointer, count) :: acc, count + 1, start_loop)
      case '<' :: next =>
        mk(next, (Op.DecPointer, count) :: acc, count + 1, start_loop)
      case '.' :: next =>
        mk(next, (Op.Output, count) :: acc, count + 1, start_loop)
      case ',' :: next =>
        mk(next, (Op.Input, count) :: acc, count + 1, start_loop)
      case '[' :: next =>
        mk(next, Nil, count + 1, Some(count)).flatMap((inner, next, count) =>
          mk(
            next,
            (Op.Loop(Block(inner.reverse)), count) :: acc,
            count + 1,
            start_loop
          )
        )
      case ']' :: next =>
        start_loop match
          case Some(start) => Right(acc, next, count)
          case None        => Left(BracketNotOpened(count))
      case c :: next =>
        mk(next, (Op.Other(c), count) :: acc, count + 1, start_loop)

  mk(s.toList, Nil, 0, None).map((ops, _, _) => Block(ops.reverse))

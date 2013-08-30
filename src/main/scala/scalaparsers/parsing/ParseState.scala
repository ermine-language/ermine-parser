package scalaparsers.parsing

import scalaparsers.parsing._

import ParsingUtil._
import scala.collection.immutable.List
import scala.collection.immutable.TreeSet
import scalaz.{ Name => _, _ }
import scalaz.Scalaz._
import scalaz.Lens._

/** Used to track the current indentation level
  *
  * @author EAK
  */

case class ParseState[S](
  loc:            Pos,
  input:          String,
  offset:         Int = 0,
  s:              S,
  layoutStack:    List[LayoutContext] = List(IndentedLayout(1,"top level")),
  bol:            Boolean = false
) extends Located {
  def depth: Int = layoutStack match {
    case IndentedLayout(n,_)   :: _ => n
    case BracedLayout(_,_,_,_) :: _ => 0
    case List()                     => 0
  }
  def layoutEndsWith: Parser[Any] = layoutStack.collectFirst({ case p : BracedLayout => p.endsWith }).getOrElse(eofIgnoringLayout scope "end of top level layout")
  def tracing = true // if we ever add an option we can add it to the case class
}

/** LayoutContext are used to track the current indentation level for parsing */
sealed abstract class LayoutContext
case class IndentedLayout(depth: Int, desc: String) extends LayoutContext {
  override def toString = "indented " + desc + " (" + depth + ")"
}
case class BracedLayout(left: String, endsWith: Parser[Any], unmatchedBy: Parser[Nothing], right: String) extends LayoutContext {
  override def toString = left + " " + right
}

object ParseState {
  def mk[S](filename: String, content: String, initialState: S) = {
    ParseState(
      loc = Pos.start(filename, content),
      input = content,
      s = initialState
    )
  }

}

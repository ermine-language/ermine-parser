package scalaparsers.parsing

import scalaparsers.parsing.Document.{ fillSep, text, punctuate, nest, line, oxford }
import scala.collection.immutable.List
import scalaz.Functor

sealed trait ParseResult[S,+A] extends Functorial[ParseResult[S],A] {
  def self = this
}

object ParseResult {
  implicit def parseResultFunctor: Functor[ParseResult[S]] = new Functor[ParseResult[S]] {
    def map[A,B](p: ParseResult[A])(f: A => B) = p map f
  }
}

/** A pure computation that hasn't consumed any input, including a list of options that could have made it eat more */
case class Pure[+A](extract: A, last: Fail = Fail()) extends ParseResult[Nothing,A] with Comonadic[Pure, A] {
  override def self = this
  def lift[B](b: Pure[B]) = b
  override def map[B](f: A => B) = Pure(f(extract), last)
  override def as[B](b: => B) = Pure(b, last)
  def extend[B](f: Pure[A] => B) = Pure(f(this), last)
}

/** A committed computation which ha manipulated the ParseState */
case class Commit[+A,S](s: ParseState[S], extract: A, expected: Set[String]) extends ParseResult[S,A] with Comonadic[Commit, A] with Located {
  override def self = this
  def lift[B](b: Commit[B]) = b
  override def map[B](f: A => B)   = Commit(s, f(extract), expected)
  override def as[B](b: => B)      = Commit(s, b, expected)
  def extend[B](f: Commit[A] => B) = Commit(s, f(this), expected)
  def loc = s.loc
}

sealed trait ParseFailure extends ParseResult[S,Nothing] {
  override def map[B](f: Nothing => B) = this
  override def as[B](b: => B) = this
}

/** A recoverable error that hasn't consumed any input */
case class Fail(msg: Option[Document] = None, aux: List[Document] = List(), expected: Set[String] = Set()) extends ParseFailure {
  override def map[B](f: Nothing => B) = this
  def ++(m: Fail) = Fail(
    m.msg orElse msg,
    if (m.msg.isDefined) m.aux
    else if (msg.isDefined) aux
    else aux ++ m.aux,
    m.expected ++ expected
  )
  def report(l: Pos): Err = Err.report(l, msg, aux, expected)
}

/** A lazily constructed error message and scope stack */
class Err(val loc: Pos, msg: => Document, val aux: List[Document], val stack: List[(Pos, String)] = List()) extends ParseFailure with Located { that =>
  lazy val message: Document = msg
  def pretty = report(message, aux :_*)
  override def toString = pretty toString
}

object Err {
  def apply(loc: Pos, msg: Document, aux: List[Document], stack: List[(Pos, String)]) = new Err(loc, msg, aux, stack)
  def unapply(err: Err) = Some((err.loc, err.message, err.aux, err.stack))
  def report(loc: Pos, msg: Option[Document], aux: List[Document], expected: Set[String] = Set()) = new Err(
    loc,
    {
      if (expected.isEmpty) msg.getOrElse("syntax error")
      else {
        val expl = "expected" :+: nest(4, oxford("or", expected.toList.sorted.map(text(_))))
        msg match {
          case Some(e) => e :: "," :/+: expl
          case None => expl
        }
      }
    },
    aux
  )
}

// vim: set ts=4 sw=4 et:

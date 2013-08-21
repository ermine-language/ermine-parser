package scala-parsers.parsing

import scala-parsers.parsing.{ Comonadic, Applied, Local, Pos, Diagnostic }
import scala-parsers.parsing.Diagnostic._
import scala-parsers.parsing.Document.text
import scala.collection.immutable.List
import scalaz.{ Name => _, Arrow => _, Free => _, Forall => _, _ }
import Scalaz._

case class Localized[+A](
  extract: A,
  names: List[Local] = List(),
  unbind: Parser[Unit] = unit(()),
  rebind: Parser[Parser[Unit]] = unit(unit(()))
) extends Comonadic[Localized, A] with Applied[Localized, A] { that =>
  def apply[B](f: A => B): Localized[B] = map(f)
  def self = that
  implicit def lift[B](v: Localized[B]) = v
  def map2[B,C](m: => Localized[B])(f: (A,B) => C): Localized[C] =
    Localized(f(extract, m.extract), names ++ m.names, unbind >> m.unbind,
              (rebind map2 m.rebind)(_ >> _))
  def extend[B](f: Localized[A] => B) = Localized(f(that), names, unbind, rebind)
  def distinct[M[+_]](loc: Pos)(implicit D:Diagnostic[M], M:Monad[M]): M[Unit] =
    raiseUnless[M](names.lengthCompare(names.toSet.size) == 0, loc,
                   "multiple bindings with names(s) " +
                   names.groupBy(identity).filter(_._2.size > 1).keys.mkString(" ")
                  )
}

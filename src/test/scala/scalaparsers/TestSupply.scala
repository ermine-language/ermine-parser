package scalaparsers

import org.scalacheck._
import Prop.{ Result => _, _ }
import scala.collection.mutable.Set

object TestSupply extends Properties("Supply") {
  class Bag {
    val xs: Set[Int] = Set()
    def put(x: Int) = synchronized {
        if (xs.contains(x)) throw new java.lang.Exception("duplicate entry " + x.toString)
        xs += x
    }
    def putFresh(implicit s: Supply) = put(s.fresh)
  }
  def supplied[A](f: Supply => A): A = f(Supply.create)


  property("lots") = secure {
    val bag = new Bag
    supplied(implicit s => for (x <- 1 to 100000) bag.putFresh)
    passed
  }
  property("split-lots") = secure {
    val bag = new Bag
    supplied(s => {
      val t = s.split
      for (x <- 1 to 10000) bag.put(t.fresh)
      for (x <- 1 to 10000) bag.put(s.fresh)
    })
    passed
  }
  property("multisplit-lots") = secure {
    val bag = new Bag
    supplied(s => {
      val t = s.split.split.split.split.split.split.split.split.split.split.split.split
      for (x <- 1 to 10000) bag.put(t.fresh)
      for (x <- 1 to 10000) bag.put(s.fresh)
    })
    passed
  }
}

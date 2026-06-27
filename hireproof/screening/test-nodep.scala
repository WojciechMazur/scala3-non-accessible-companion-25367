//> using file cats/data/Newtype2.scala
//> using file cats/data/package.scala
//> using file cats/data/nonemptymap.scala
//> using file cats/syntax/package.scala

import cats.data.NonEmptyMap
import cats.syntax.all._

@main def test(): Unit =
  val nem = NonEmptyMap.one("a", 1)
  val _ = nem.mapKeys(identity)
  val _ = nem("a")

//> using dep org.typelevel::cats-core:2.7.0

import cats.data.NonEmptyMap
import cats.syntax.all._

@main def test(): Unit =
  val nem = NonEmptyMap.one("a", 1)
  val _ = nem.mapKeys(identity)
  val _ = nem("a")

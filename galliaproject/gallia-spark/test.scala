//> using dep io.github.galliaproject::gallia-core:0.6.1

import gallia._

@main def test(): Unit =
  val _ = bobj("foo" -> "BAR1", "baz" -> "1")

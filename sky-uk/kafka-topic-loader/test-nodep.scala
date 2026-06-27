//> using file cats/data/package.scala
//> using file cats/data/ScalaVersionSpecificNonEmptyChainImpl.scala
//> using file cats/data/nonemptychain.scala

import cats.data.{Validated, ValidatedNec}

def showErrors(v: ValidatedNec[String, Int]): String = v match
  case Validated.Valid(n)   => n.toString
  case Validated.Invalid(e) => e.toNonEmptyList.toList.mkString(", ")

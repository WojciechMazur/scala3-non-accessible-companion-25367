//> using dep org.typelevel::cats-core:2.7.0

import cats.data.{Validated, ValidatedNec}
import cats.implicits.*

def showErrors(v: ValidatedNec[String, Int]): String = v match {
  case Validated.Valid(n)   => n.toString
  case Validated.Invalid(e) => e.toNonEmptyList.toList.mkString(", ")
}

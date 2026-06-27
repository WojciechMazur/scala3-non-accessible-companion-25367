//> using dep org.typelevel::cats-core:2.6.1

import cats.data.NonEmptyChain

type Errors[E] = NonEmptyChain[E]

def merge[E](e1: Errors[E], e2: Errors[E]): Errors[E] =
  e1 ++ e2

def mapErrors(errors: Errors[Int]): Errors[String] =
  errors.map(_.toString)

@main def test(): Unit =
  val _ = merge(NonEmptyChain(1), NonEmptyChain(2))
  val _ = mapErrors(NonEmptyChain(1))

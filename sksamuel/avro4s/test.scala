//> using dep org.typelevel::cats-core:2.7.0

import cats.data.NonEmptyChain

def encodeChain[T, U](encodeFn: T => U): NonEmptyChain[T] => List[U] =
  val encode = encodeFn
  { value => value.map(encode).toNonEmptyList.toList }

@main def test(): Unit =
  val _ = encodeChain((i: Int) => i.toString)(NonEmptyChain.one(1))

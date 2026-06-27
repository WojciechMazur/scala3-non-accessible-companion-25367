//> using dep org.typelevel::cats-core:2.6.1

import cats.data.NonEmptyChain
import cats.implicits.*

enum Suspendable[S, A]:
  case Rep(source: Suspendable[S, A]) extends Suspendable[S, NonEmptyChain[A]]

  def rep: Suspendable[S, NonEmptyChain[A]] = Rep(this)

  def map[B](f: A => B): Suspendable[S, B] = ???

def combineAllTrigger[S, A: cats.Monoid](p: Suspendable[S, A]): Suspendable[S, A] =
  p.rep.map(_.combineAll)

package prometheus4cats.internal

sealed trait InitLast[A, B]:
  type C <: Product
  def init(c: C): A
  def last(c: C): B

trait LowPriorityInitLast:
  implicit def base[A, B]: InitLast.Aux[A, B, (A, B)] =
    InitLast.make(_._1, _._2)

private[prometheus4cats] object InitLast extends PlatformSpecificInitLast:
  type Aux[A, B, Out] = InitLast[A, B] { type C = Out }

  private[internal] def make[A, B, Out <: Product](
      _init: Out => A,
      _last: Out => B
  ): InitLast.Aux[A, B, Out] = new InitLast[A, B]:
    type C = Out
    def init(c: C): A = _init(c)
    def last(c: C): B = _last(c)

private[prometheus4cats] trait PlatformSpecificInitLast extends LowPriorityInitLast:
  type NonEmptyAppend[X <: Tuple, Y] <: NonEmptyTuple = X match
    case EmptyTuple => Y *: EmptyTuple
    case x *: xs    => x *: NonEmptyAppend[xs, Y]

  implicit def default[A <: NonEmptyTuple, B]: InitLast.Aux[A, B, NonEmptyAppend[A, B]] =
    InitLast.make(_.init.asInstanceOf[A], _.last.asInstanceOf[B])

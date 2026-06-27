package cats.data

private[data] object NonEmptyChainImpl extends ScalaVersionSpecificNonEmptyChainImpl:
  private[data] type Base
  private[data] trait Tag extends Any
  type Type[+A] <: Base with Tag

  private[data] def create[A](s: List[A]): Type[A] = s.asInstanceOf[Type[A]]
  private[data] def unwrap[A](s: Type[A]): List[A] = s.asInstanceOf[List[A]]

  def one[A](a: A): NonEmptyChain[A] = create(List(a))
  def apply[A](head: A, tail: A*): NonEmptyChain[A] = create(head :: tail.toList)

  implicit def catsNonEmptyChainOps[A](value: NonEmptyChain[A]): NonEmptyChainOps[A] =
    new NonEmptyChainOps(value)

class NonEmptyChainOps[A](private val value: NonEmptyChain[A]) extends AnyVal:
  def map[B](f: A => B): NonEmptyChain[B] =
    NonEmptyChainImpl.create(NonEmptyChainImpl.unwrap(value).map(f))
  def ++[A2 >: A](that: NonEmptyChain[A2]): NonEmptyChain[A2] =
    NonEmptyChainImpl.create(NonEmptyChainImpl.unwrap(value) ++ NonEmptyChainImpl.unwrap(that))

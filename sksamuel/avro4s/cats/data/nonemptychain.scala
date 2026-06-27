package cats.data

private[data] object NonEmptyChainImpl extends ScalaVersionSpecificNonEmptyChainImpl:
  private[data] type Base
  private[data] trait Tag extends Any
  type Type[+A] <: Base with Tag

  private[data] def create[A](s: List[A]): Type[A] = s.asInstanceOf[Type[A]]
  private[data] def unwrap[A](s: Type[A]): List[A] = s.asInstanceOf[List[A]]

  def one[A](a: A): NonEmptyChain[A] = create(List(a))

  implicit def catsNonEmptyChainOps[A](value: NonEmptyChain[A]): NonEmptyChainOps[A] =
    new NonEmptyChainOps(value)

class NonEmptyChainOps[A](private val value: NonEmptyChain[A]) extends AnyVal:
  def map[B](f: A => B): NonEmptyChain[B] =
    NonEmptyChainImpl.create(NonEmptyChainImpl.unwrap(value).map(f))
  def toNonEmptyList: NonEmptyList[A] =
    NonEmptyList.fromListUnsafe(NonEmptyChainImpl.unwrap(value))

final case class NonEmptyList[+A](head: A, tail: List[A] = Nil):
  def toList: List[A] = head :: tail

object NonEmptyList:
  def fromListUnsafe[A](as: List[A]): NonEmptyList[A] = NonEmptyList(as.head, as.tail)

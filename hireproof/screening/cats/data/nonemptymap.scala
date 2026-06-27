package cats.data

private[data] object NonEmptyMapImpl extends Newtype2:
  private[data] def create[K, A](m: Map[K, A]): Type[K, A] = m.asInstanceOf[Type[K, A]]
  private[data] def unwrap[K, A](m: Type[K, A]): Map[K, A] = m.asInstanceOf[Map[K, A]]

  def one[K, A](k: K, a: A): NonEmptyMap[K, A] = create(Map(k -> a))

  implicit def catsNonEmptyMapOps[K, A](value: Type[K, A]): NonEmptyMapOps[K, A] =
    new NonEmptyMapOps(value)

sealed class NonEmptyMapOps[K, A](val value: NonEmptyMap[K, A]):
  def mapKeys[L](f: K => L): NonEmptyMap[L, A] =
    NonEmptyMapImpl.create(NonEmptyMapImpl.unwrap(value).map { case (k, v) => f(k) -> v })
  def apply(key: K): A = NonEmptyMapImpl.unwrap(value)(key)

package cats

package object data:
  type NonEmptyMap[K, +A] = NonEmptyMapImpl.Type[K, A]
  val NonEmptyMap = NonEmptyMapImpl

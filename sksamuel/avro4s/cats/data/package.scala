package cats

package object data:
  type NonEmptyChain[+A] = NonEmptyChainImpl.Type[A]
  val NonEmptyChain = NonEmptyChainImpl

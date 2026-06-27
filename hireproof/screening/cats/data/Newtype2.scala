package cats.data

private[data] trait Newtype2:
  private[data] type Base
  private[data] trait Tag extends Any
  type Type[A, +B] <: Base with Tag

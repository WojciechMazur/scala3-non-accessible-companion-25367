package cats

trait Monoid[A]:
  def empty: A
  def combine(x: A, y: A): A

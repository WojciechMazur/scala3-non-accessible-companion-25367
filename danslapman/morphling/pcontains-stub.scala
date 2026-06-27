package glass

trait PContains[-S, +T, +A, -B]:
  def extract(s: S): A
  def set(s: S, b: B): T

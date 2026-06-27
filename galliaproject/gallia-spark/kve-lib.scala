package gallia.domain

import gallia.reflect.lowlevel.WTT

sealed trait KVE

object KVE:
  implicit def toValueEntryS[T: WTT](pair: (String, T)): KVE =
    KVEImpl(pair._1, pair._2)

  private case class KVEImpl(key: String, value: Any) extends KVE

case class BObj(entries: KVEs)
case class KVEs(values: List[KVE])

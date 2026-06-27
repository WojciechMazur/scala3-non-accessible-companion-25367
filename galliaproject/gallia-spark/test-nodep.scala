//> using file wtt-lib.scala
//> using file kve-lib.scala

package gallia

import gallia.domain.*
import gallia.domain.KVE.given
import gallia.reflect.lowlevel.given

def bobj(entry1: KVE, more: KVE*): BObj = BObj(KVEs((entry1 +: more).toList))

@main def test(): Unit =
  val _ = bobj("foo" -> "BAR1", "baz" -> "1")

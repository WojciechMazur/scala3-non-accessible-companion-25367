package gallia.reflect.lowlevel

trait WTT[T]

given WTT[Int] = new WTT[Int] {}
given WTT[String] = new WTT[String] {}

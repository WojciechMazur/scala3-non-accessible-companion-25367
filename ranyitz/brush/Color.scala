package brush

private trait Color[A]:
  def toRGB(param: A): (Int, Int, Int)

private object Color:
  implicit object StringColor extends Color[String]:
    def toRGB(param: String): (Int, Int, Int) = (0, 0, 0)

  implicit object RGBColor extends Color[(Int, Int, Int)]:
    def toRGB(param: (Int, Int, Int)): (Int, Int, Int) = param

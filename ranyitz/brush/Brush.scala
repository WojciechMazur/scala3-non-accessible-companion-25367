package brush

class Brush:
  implicit def decorate(str: String): BrushMethods = new BrushMethods(str)

  protected class BrushMethods(str: String):
    def gradient[A](colors: A*)(implicit color: Color[A]): String =
      colors.map(color.toRGB).mkString(",")

object Brush extends Brush

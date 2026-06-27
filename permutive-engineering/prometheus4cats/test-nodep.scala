package prometheus4cats.internal

trait Show[A]:
  def show(a: A): String

object Show:
  given stringShow: Show[String] = _.toString
  given intShow: Show[Int]       = _.toString

class LabelName(val value: String)

class LabelledMetricDsl[T](labelNames: List[LabelName], f: T => List[String]):
  def label[B]: LabelApply[T, B] = new LabelApply[T, B](labelNames, f)

class LabelApply[T, B](labelNames: List[LabelName], f: T => List[String]):
  def apply[C](name: LabelName)(using
      show: Show[B],
      initLast: InitLast.Aux[T, B, C]
  ): LabelledMetricDsl[C] =
    new LabelledMetricDsl(labelNames :+ name, c => f(initLast.init(c)) :+ show.show(initLast.last(c)))

@main def test(): Unit =
  val _ =
    new LabelledMetricDsl[Unit](Nil, _ => Nil)
      .label[String]
      .apply(LabelName("label1"))
      .label[Int]
      .apply(LabelName("label2"))

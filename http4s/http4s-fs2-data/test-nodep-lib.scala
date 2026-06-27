//> using dep org.typelevel::shapeless3-deriving:3.6.0
// Inlined from fs2-data-csv-generic 1.10.0 (Names, semiauto). Pre-compile to jar for test-nodep.scala.

package fs2:
  package data:
    package csv:
      trait CellEncoder[T]:
        def apply(t: T): String

      object CellEncoder:
        given CellEncoder[String] = _.toString
        given CellEncoder[Long] = _.toString
        given CellEncoder[Boolean] = _.toString

      trait CsvRowEncoder[T, H]
      trait CsvRowDecoder[T, H]

      package generic:
        import scala.annotation.Annotation

        case class CsvName(name: String) extends Annotation

        package internal:
          import shapeless3.deriving.*

          private[generic] trait Names[T]:
            def names: List[String]

          private[generic] object Names:
            given [T](using labels: Labelling[T], annotations: Annotations[CsvName, T]): Names[T] =
              new Names[T]:
                override def names: List[String] =
                  val annos = annotations.apply().toList.asInstanceOf[List[Option[CsvName]]]
                  val fieldNames = labels.elemLabels.toList
                  annos.zip(fieldNames).map(_.map(_.name).getOrElse(_))

        object semiauto:
          import shapeless3.deriving.K0

          def deriveCsvRowEncoder[T](using
              ic: K0.ProductInstances[CellEncoder, T],
              naming: generic.internal.Names[T]): CsvRowEncoder[T, String] =
            new CsvRowEncoder[T, String] {}

          def deriveCsvRowDecoder[T](using
              ic: K0.ProductInstances[CellEncoder, T],
              naming: generic.internal.Names[T]): CsvRowDecoder[T, String] =
            new CsvRowDecoder[T, String] {}

//> using dep org.typelevel::shapeless3-deriving:3.6.0
// Trigger for pre-built test-nodep-lib.jar (see notes.md).

package example:
  package game:
    package csv:
      import fs2.data.csv.*
      import fs2.data.csv.generic.semiauto.*

      case class Row(a: String)

      object Row:
        given CsvRowEncoder[Row, String] = deriveCsvRowEncoder

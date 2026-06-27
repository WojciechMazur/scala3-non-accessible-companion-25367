//> using dep org.typelevel::shapeless3-deriving:3.6.0
// Trigger for pre-built test-nodep-lib.jar (see notes.md).

package org:
  package http4s:
    package fs2data:
      package csv:
        import fs2.data.csv.*
        import fs2.data.csv.generic.semiauto

        case class Data(first: String, second: Long, third: Boolean)

        object Data:
          given CsvRowEncoder[Data, String] = semiauto.deriveCsvRowEncoder
          given CsvRowDecoder[Data, String] = semiauto.deriveCsvRowDecoder

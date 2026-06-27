//> using dep org.gnieh::fs2-data-csv-generic:1.14.0

package example.game.csv

import fs2.data.csv.*
import fs2.data.csv.generic.semiauto.*

case class Row(a: String)

object Row:
  given CsvRowEncoder[Row, String] = deriveCsvRowEncoder

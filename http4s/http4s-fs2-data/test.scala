//> using dep org.gnieh::fs2-data-csv-generic:1.10.0

package org.http4s.fs2data.csv

import fs2.data.csv.*
import fs2.data.csv.generic.semiauto

case class Data(first: String, second: Long, third: Boolean)

object Data:
  given CsvRowEncoder[Data, String] = semiauto.deriveCsvRowEncoder
  given CsvRowDecoder[Data, String] = semiauto.deriveCsvRowDecoder

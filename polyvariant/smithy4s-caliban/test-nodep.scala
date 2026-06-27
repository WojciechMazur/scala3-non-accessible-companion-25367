//> using file caliban-stub.scala

import caliban.ResponseValue
import caliban.Value.StringValue
import io.circe.syntax.*

val rv: ResponseValue = StringValue("hi")
val json = rv.asJson

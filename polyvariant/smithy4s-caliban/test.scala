//> using dep com.github.ghostdogpr::caliban:2.2.1
//> using dep io.circe::circe-core:0.14.5

import caliban.ResponseValue
import caliban.Value.StringValue
import io.circe.syntax._

val rv: ResponseValue = StringValue("hi")
val json = rv.asJson

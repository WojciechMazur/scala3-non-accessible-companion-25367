//> using dep tf.tofu::glass-core:0.3.0
//> using dep tf.tofu::glass-macro:0.3.0

import glass.macros.*

case class Person(name: String)

object Person extends DeriveContains {
  val nameField = Person.name
}

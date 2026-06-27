//> using file quotidian-stub.scala
//> using file quotidian-syntax-stub.scala
//> using file pcontains-stub.scala
//> using file glass-impl-stub.scala
//> using file contains-selector-stub.scala
//> using file derive-contains-stub.scala

import glass.macros.*

case class Person(name: String)

object Person extends DeriveContains:
  val nameField = Person.name

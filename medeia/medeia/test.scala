//> using dep de.megaera::medeia:1.0.9

import medeia.codec.*

class Spec:
  sealed trait Trait derives BsonDocumentCodec
  case class A(stringField: String) extends Trait
  case class B(int: Int) extends Trait

  enum TestEnum derives BsonDocumentCodec:
    case A1(stringField: String)
    case B2(int: Int)

import scala.compiletime.*
import scala.deriving.Mirror

package medeia.codec:

  trait BsonDocumentCodec[A]

  object BsonDocumentCodec:
    inline given derived[A]: BsonDocumentCodec[A] = derivedImpl[A]

    inline def derivedImpl[A]: BsonDocumentCodec[A] =
      val enc = summonInline[medeia.generic.GenericEncoder[A]]
      new BsonDocumentCodec[A] {}

package medeia.generic:

  trait Labelling[A]:
    def elemLabels: List[String]

  object Labelling:
    given instance[A]: Labelling[A] = new Labelling[A]:
      def elemLabels: List[String] = Nil

  trait ProductInstances[F[_], A]

  object ProductInstances:
    inline given derived[A](using m: Mirror.ProductOf[A]): ProductInstances[GenericEncoder, A] =
      new ProductInstances[GenericEncoder, A] {}

  trait GenericEncoder[A]:
    def encode(a: A): Unit = ()

  object GenericEncoder extends GenericEncoderInstances

  trait GenericEncoderInstances:
    given coproduct[A](using m: Mirror.SumOf[A], lab: Labelling[A]): GenericEncoder[A] =
      new GenericEncoder[A] {}

    given product[A: Labelling](using inst: => ProductInstances[GenericEncoder, A]): GenericEncoder[A] =
      new GenericEncoder[A] {}

class Spec:
  sealed trait Trait derives medeia.codec.BsonDocumentCodec
  case class A(stringField: String) extends Trait
  case class B(int: Int) extends Trait

  enum TestEnum derives medeia.codec.BsonDocumentCodec:
    case A1(stringField: String)
    case B2(int: Int)

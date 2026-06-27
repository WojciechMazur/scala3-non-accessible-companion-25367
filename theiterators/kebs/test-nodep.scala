//> using dep org.scalacheck::scalacheck:1.19.0
//> using options -Xmax-inlines:64
// Trigger for pre-built test-nodep-lib.jar (see notes.md).

import org.scalacheck.*
import io.github.martinhh.derived.scalacheck.*
import scala.deriving.Mirror

trait Support:
  inline implicit def arb[T](using inline mirror: Mirror.Of[T]): Arbitrary[T] = deriveArbitrary[T]

enum Greeting:
  case Hello, GoodBye, Hi, Bye

case class BasicSample(greeting: Greeting)

object Test extends Support:
  val _ = summon[Arbitrary[BasicSample]]

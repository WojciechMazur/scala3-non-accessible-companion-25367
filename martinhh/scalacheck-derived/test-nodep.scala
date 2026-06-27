//> using dep org.scalacheck::scalacheck:1.19.0
// Trigger for pre-built test-nodep-lib.jar (see notes.md).

import io.github.martinhh.derived.arbitrary.deriveArbitrary
import org.scalacheck.Arbitrary

enum RecursiveList[+T]:
  case Cns(t: T, ts: RecursiveList[T])
  case Nl

val _: Arbitrary[RecursiveList[Int]] = deriveArbitrary

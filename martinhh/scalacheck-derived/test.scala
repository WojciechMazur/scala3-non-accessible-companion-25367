//> using dep org.scalacheck::scalacheck:1.19.0
//> using dep io.github.martinhh::scalacheck-derived:0.10.0

import io.github.martinhh.derived.arbitrary.deriveArbitrary
import org.scalacheck.Arbitrary

enum RecursiveList[+T]:
  case Cns(t: T, ts: RecursiveList[T])
  case Nl

val _: Arbitrary[RecursiveList[Int]] = deriveArbitrary

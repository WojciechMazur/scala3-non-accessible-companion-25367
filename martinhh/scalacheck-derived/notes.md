# martinhh/scalacheck-derived — recursive sum Summoner (cluster C3)

## Original failure

- **Project**: `martinhh/scalacheck-derived` v0.10.0
- **Module**: `scalacheck-derived` (test-compile)
- **File**: `core/src/test/scala/io/github/martinhh/ArbitraryDerivingSuite.scala:18`
- **Trigger**: `derived.arbitrary.deriveArbitrary` on recursive `enum RecursiveList[+T]`

## Error

```
[E172] Type Error: ... ArbitraryDerivingSuite.scala:18:63
Derivation failed. No given instance of type Summoner[RecursiveList.Cns[Int]] was found.
This is most likely due to no Arbitrary[RecursiveList.Cns[Int]] being available

Inline stack trace from ArbitraryDeriving.scala:106
  val elems = summonAll[Tuple.Map[s.MirroredElemTypes, Summoner]].toList
```

Related failures in the same build (same root cause, different typeclasses / shapes):

- `MaybeMaybeList` — `anyGivenArbitrary` found but "does not match" (`ArbitraryDeriving.scala:94`, product `summonAll`)
- `ComplexADTWithNestedMembers => ABC` — `cannot reduce summonFrom` (`ArbitraryDeriving.scala:268`)
- Same `Summoner[Cns[Int]]` pattern for `Cogen` / `Shrink` derivation

## Reproducer (library-dependent)

```bash
scala-cli compile --server=false test.scala -S 3.8.4
scala-cli compile --server=false test.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Passes on 3.8.4; fails on nightly with E172 at inlined `summonAll[Tuple.Map[..., Summoner]]`.

## Dependency-free reproducer

Vendored scalacheck-derived 0.10.0 sources under `nodep-lib/` (via `test-nodep-lib.scala`). Only external dep is `org.scalacheck::scalacheck:1.19.0` (stdlib-like stub for `Arbitrary` / `Gen`). Pre-compile the derivation library to jar:

```bash
cd repro/martinhh/scalacheck-derived
scala-cli compile --server=false test-nodep-lib.scala -S 3.8.4 -d .nodep-out
(cd .nodep-out && jar cf ../.nodep-lib.jar .)
scala-cli compile --server=false test-nodep.scala -S 3.8.4 --extra-jars .nodep-lib.jar
scala-cli compile --server=false test-nodep.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY --extra-jars .nodep-lib.jar
```

Passes on 3.8.4; fails on nightly with the same E172 `Summoner[RecursiveList.Cns[Int]]` error.

## Cluster

**C3** — inline derivation: `summonAll` fails to resolve `Summoner` for recursive sum enum case classes (and related recursive-given / `summonFrom` breakage).

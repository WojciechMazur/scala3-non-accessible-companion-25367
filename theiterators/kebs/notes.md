# theiterators/kebs — inline deriveArbitrary enum failure

## Original failure

- **Project**: `theiterators/kebs` v2.1.6
- **Modules**: `kebs-scalacheck`, `kebs-tagged-meta`, `kebs-http4s`, `kebs-http4s-stir`, `kebs-pekko-http` (all share the same scalacheck test)
- **File**: `scalacheck/src/test/scala-3/pl/iterators/kebs/scalacheck/OpaqueGeneratorsTests.scala:12`
- **Trigger**: `generate[BasicSample]()` with `KebsArbitrarySupport` (`inline implicit def arb[T] = deriveArbitrary[T]`)

## Error

```
[E172] Type Error: OpaqueGeneratorsTests.scala:12:58
No given instance of type org.scalacheck.Arbitrary[...model.Greeting] was found.
I found: KebsArbitrarySupport.arb[Greeting](...)
But method arb in trait ScalacheckInstancesSupport does not match type org.scalacheck.Arbitrary[...Greeting]
```

Inline stack trace points to `ArbitraryDeriving.scala:95` inside `scalacheck-derived`.

## Reproducer (library-dependent)

```bash
scala-cli compile --server=false test.scala -S 3.8.4
scala-cli compile --server=false test.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Passes on 3.8.4; fails on nightly with E172.

## Dependency-free reproducer

Same vendored scalacheck-derived 0.10.0 sources as [martinhh/scalacheck-derived](../martinhh/scalacheck-derived/test-nodep-lib.scala). Trigger uses `Support` + `summon[Arbitrary[BasicSample]]` (kebs `KebsArbitrarySupport` shape).

```bash
cd repro/theiterators/kebs
scala-cli compile --server=false test-nodep-lib.scala -S 3.8.4 -d .nodep-out
(cd .nodep-out && jar cf ../.nodep-lib.jar .)
scala-cli compile --server=false test-nodep.scala -S 3.8.4 --extra-jars .nodep-lib.jar
scala-cli compile --server=false test-nodep.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY --extra-jars .nodep-lib.jar
```

Passes on 3.8.4; fails on nightly with the same E172 (`arb` does not match `Arbitrary[Greeting]`).

## Cluster

**C3** — inline `deriveArbitrary` for enum sum types fails to satisfy `Arbitrary[T]` during implicit search (same root cause as scalacheck-derived).

## Community-build boundary

```bash
cd repro/theiterators/kebs
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh theiterators/kebs 3.8.4          # success
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh theiterators/kebs 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY  # 5 modules test-compile failed
```

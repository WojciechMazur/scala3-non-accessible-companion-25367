# polyvariant/smithy4s-caliban — caliban ResponseValue.asJson (cluster C3)

## Original failure

- **Project**: `polyvariant/smithy4s-caliban` (v0.1.0)
- **Module**: `smithy4s-caliban` (test-compile)
- **File**: `modules/core/src/test/scala/org/polyvariant/smithy4scaliban/CalibanTestUtils.scala:39,51`
- **Trigger**: `.map(_.data.asJson)` on GraphQL execute results

## Error

```
[E172] Type Error: CalibanTestUtils.scala:39:22
No given instance of type io.circe.Encoder[caliban.ResponseValue] was found for parameter encoder of method asJson in class EncoderOps.
I found:
    caliban.ResponseValue.circeDecoder[F](
      /* missing */summon[caliban.interop.circe².IsCirceDecoder[F]])
But no implicit values were found that match type caliban.interop.circe².IsCirceDecoder[F]

where:    circe  is a package in package io
          circe² is a package in package caliban.interop
```

Caliban's no-more-orphans marker `IsCirceEncoder[Encoder]` (a null implicit in `caliban.interop.circe`) is not resolved when `io.circe.syntax._` is in scope; the compiler disambiguates `circe` vs `circe²` and fails to wire `ResponseValue.circeEncoder`.

## Reproducer

```bash
scala-cli compile --server=false test.scala -S 3.8.4
scala-cli compile --server=false test.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Passes on 3.8.4; fails on nightly with E172.

## Dependency-free reproducer

`caliban-stub.scala` inlines `IsCirceEncoder`, `ResponseValue.circeEncoder`, and minimal `io.circe` syntax; `test-nodep.scala` calls `rv.asJson`.

```bash
scala-cli compile --server=false test-nodep.scala -S 3.8.4
scala-cli compile --server=false test-nodep.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Verified: passes 3.8.4, fails nightly with E008 on `asJson` whose expansion reports missing `IsCirceEncoder[F]` / `Encoder[ResponseValue]` (same root cause as E172 in `test.scala`).

## Cluster

**C3** — final package segment `circe` collides between `io.circe` and `caliban.interop.circe`, breaking orphan-marker implicit lookup for `Encoder[ResponseValue]` via `asJson`.

## Community-build boundary

```bash
cd repro/polyvariant/smithy4s-caliban
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh polyvariant/smithy4s-caliban 3.8.4          # test-compile ok, 45 tests pass
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh polyvariant/smithy4s-caliban 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY  # test-compile failed (2 errors)
```

Main sources compile on both versions; only test sources fail on nightly.

# medeia/medeia — nested sum type `derives BsonDocumentCodec` (cluster C3)

## Original failure

- **Project**: `medeia/medeia` @ v1.0.9
- **Module**: `medeia` (core)
- **File**: `core/src/test/scala-3/medeia/Scala3DerivesSpec.scala:28,56`
- **Trigger**: nested sealed trait / enum with `derives BsonDocumentCodec` inside a test class

## Error

```
[E172] Type Error: Scala3DerivesSpec.scala:28:29
Could not derive BsonDocumentEncoder for Scala3DerivesSpec.this.Trait.
...
summon[scala.deriving.Mirror.Product of Scala3DerivesSpec.this.Trait]
But Failed to synthesize ... trait Trait is not a generic product because it is not a case class

[E172] Type Error: Scala3DerivesSpec.scala:56:24
Could not derive BsonDecoder for Scala3DerivesSpec.this.TestEnum.
...
summon[scala.deriving.Mirror.Product of Scala3DerivesSpec.this.TestEnum]
But Failed to synthesize ... class TestEnum is not a generic product because it is not a case class
```

On nightly, `summonInline` for medeia generic codecs selects the **product** given (via `Labelling`) instead of the **coproduct** given for sum types declared inside an outer class. Resolution then fails on missing `Mirror.Product`.

## Reproducer

```bash
scala-cli compile --server=false test.scala -S 3.8.4
scala-cli compile --server=false test.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Passes on 3.8.4; fails on nightly with 2× E172. Depends on published `de.megaera::medeia:1.0.9` (shapeless3 generic derivation).

## Dependency-free reproducer

`test-nodep.scala` inlines `BsonDocumentCodec.derived`, `GenericEncoder` product/coproduct givens, and `Labelling`.

```bash
scala-cli compile --server=false test-nodep.scala -S 3.8.4
scala-cli compile --server=false test-nodep.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Status: **partial** — passes both versions. Nested sum-type `derives` needs shapeless3 `K0`/`ProductInstances` from the published library to reproduce the nightly product-over-coproduct E172. Use `test.scala` for the authoritative bad compile.

## Cluster

**C3** — nested sum type + `derives` codec: product given preferred over coproduct; `Mirror.Product` missing.

## Community-build boundary

```bash
cd repro/medeia/medeia
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh medeia/medeia 3.8.4          # medeia test-compile ok
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh medeia/medeia 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY  # medeia test-compile failed (2 errors)
```

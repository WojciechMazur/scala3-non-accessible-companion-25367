# Triage — non-accessible companion implicits (#25367)

Dashboard: [compare b34a019 → 20f6657](https://scala3.westeurope.cloudapp.azure.com/dashboard/compare?baseScalaVersion=3.10.0-RC1-bin-20260609-b34a019-NIGHTLY&targetScalaVersion=3.10.0-RC1-bin-20260626-20f6657-NIGHTLY&reason=Compilation)

**Known good:** 3.8.4 | **Known bad:** `3.10.0-RC1-bin-20260626-20f6657-NIGHTLY` (and from `4733954` for this commit)

Bisect: **`5321891783b8d432f0a25d9cdf0be01ed444194d`** — see [BISECT.md](BISECT.md).

## Error clusters (by symptom)

| Cluster | Primary error | Signature | Canonical repro |
|---------|---------------|-----------|-----------------|
| **C2** | E172 | `fs2.data.csv.generic.internal.Names` — package `csv` shadows `fs2.data.csv` | [lichess-org/lila-search/test.scala](lichess-org/lila-search/test.scala) |
| **C3** | E172 | Inline `summonAll` / `Summoner` / `deriveArbitrary` on recursive sums | [martinhh/scalacheck-derived/test.scala](martinhh/scalacheck-derived/test.scala) |
| **C4** | E008 | `NonEmptyChain` / `NonEmptyMap` extension methods via alias or pattern match | [sksamuel/avro4s/test.scala](sksamuel/avro4s/test.scala) |
| **C5** | E172 | Cross-package or nested-package companion givens not found | [sbt/io/test.scala](sbt/io/test.scala) |
| **C6** | E007 | gallia tuple → `KVE` (`[T: WTT]` implicits) | [galliaproject/gallia-core/test.scala](galliaproject/gallia-core/test.scala) |
| **C7** | E008 | `glass.macros.internal.CompanionClass` for `DeriveContains` | [danslapman/morphling/test.scala](danslapman/morphling/test.scala) |
| **C8** | E172 | kevin-lee `OrphanCats` conditional HK givens | [kevin-lee/extras/test.scala](kevin-lee/extras/test.scala) |
| **C9** | E172 | medeia `derives BsonDocumentCodec` product vs coproduct | [medeia/medeia/test.scala](medeia/medeia/test.scala) |
| **C10** | E172 | prometheus4cats `InitLast.Aux` label DSL | [permutive-engineering/prometheus4cats/test.scala](permutive-engineering/prometheus4cats/test.scala) |
| **C11** | E172 | caliban / `io.circe` package collision | [polyvariant/smithy4s-caliban/test.scala](polyvariant/smithy4s-caliban/test.scala) |

## Per-project triage

| Project | Error | Summary | Cluster | Repro |
|---------|-------|---------|---------|-------|
| lichess-org/lila-search | E172 | `deriveCsvRowEncoder` Names derivation | C2 | [test.scala](lichess-org/lila-search/test.scala) |
| http4s/http4s-fs2-data | E172 | same Names bug (`org.http4s.fs2data.csv`) | C2 | [notes](http4s/http4s-fs2-data/notes.md) |
| martinhh/scalacheck-derived | E172 | inline `summonAll[Summoner]` on recursive enum | C3 | [test.scala](martinhh/scalacheck-derived/test.scala) |
| theiterators/kebs | E172 | inline `deriveArbitrary` enum sum mismatch | C3 | [test.scala](theiterators/kebs/test.scala) |
| sksamuel/avro4s | E008 | `NonEmptyChain.map` in nested closure | C4 | [test.scala](sksamuel/avro4s/test.scala) |
| koterpillar/refinery | E008 | `NonEmptyChain` `++` via type alias | C4 | [test.scala](koterpillar/refinery/test.scala) |
| noelwelsh/mads | E008 | enum case / `NonEmptyChainImpl.Type` | C4 | [test.scala](noelwelsh/mads/test.scala) |
| sky-uk/kafka-topic-loader | E008 | `ValidatedNec` / `NonEmptyChainImpl.Type` | C4 | [test.scala](sky-uk/kafka-topic-loader/test.scala) |
| hireproof/screening | E008/E050 | legacy cats `NonEmptyMap` ops | C4 | [test.scala](hireproof/screening/test.scala) |
| sbt/io | E172 | cross-package `TimeSource` given | C5 | [test.scala](sbt/io/test.scala) |
| ranyitz/brush | E172 | private `Color` companion givens | C5 | [test.scala](ranyitz/brush/test.scala) |
| snowplow/snowplow-scala-analytics-sdk | E172 | `summonInline` nested decoder | C5 | [test.scala](snowplow/snowplow-scala-analytics-sdk/test.scala) |
| galliaproject/gallia-core | E007 | `bobj("g" -> 1)` tuple→KVE | C6 | [test.scala](galliaproject/gallia-core/test.scala) |
| galliaproject/gallia-spark | E007 | same KVE/WTT regression | C6 | [test.scala](galliaproject/gallia-spark/test.scala) |
| danslapman/morphling | E008 | glass `CompanionClass` | C7 | [test.scala](danslapman/morphling/test.scala) |
| kevin-lee/extras | E172 | conditional HK `Contravariant[Render]` | C8 | [test.scala](kevin-lee/extras/test.scala) |
| kevin-lee/logger-f | E172 | orphan-cats `Log[F]` | C8 | [test.scala](kevin-lee/logger-f/test.scala) |
| medeia/medeia | E172 | nested sum `derives BsonDocumentCodec` | C9 | [test.scala](medeia/medeia/test.scala) |
| permutive-engineering/prometheus4cats | E172 | `InitLast.Aux` label DSL | C10 | [test.scala](permutive-engineering/prometheus4cats/test.scala) |
| polyvariant/smithy4s-caliban | E172 | caliban circe collision on `asJson` | C11 | [test.scala](polyvariant/smithy4s-caliban/test.scala) |

## `test-nodep.scala` notes

| Status | Projects |
|--------|----------|
| Full nodep repro | 17 (pass 3.8.4, fail nightly) |
| Partial nodep | gallia-core/spark, medeia, prometheus4cats — use `test.scala` for authoritative nightly failure |

C4 nodep repros use inlined `cats/data` stubs. C2/C3 use `test-nodep-lib.scala` → `.nodep-lib.jar` workflow (see [README.md](README.md)).

## Cluster notes

- **C2**: `http4s-fs2-data` shares root cause with lila-search; canonical minimal case is lila-search.
- **C3**: `kebs` and `scalacheck-derived` share inline derivation theme; separate library repros.
- **C4**: five projects, one `NonEmptyChainImpl.Type` theme; separate call-site shapes.
- **C6**: gallia-core and gallia-spark share identical 6-line repro.

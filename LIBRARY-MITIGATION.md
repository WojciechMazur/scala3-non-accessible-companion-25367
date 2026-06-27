# OSS library mitigation summary (#25367)

For commit **`5321891`** / [#25367](https://github.com/scala/scala3/pull/25367) (non-accessible companion implicits), **14 OSS libraries** likely need a **library-side** change to mitigate without a compiler fix. **avro4s-cats** may only need a small import if **cats** is fixed upstream.

**cats** accounts for **6** of the 20 community-build failures (C4 `NonEmptyChain` / `NonEmptyMap` cluster plus **avro4s**).

---

## 1. cats — `org.typelevel::cats-core`

**Community-build consumers:** avro4s, refinery, mads, kafka-topic-loader, hireproof/screening (+ `Validated.mapN` in kafka-topic-loader)

| Missing / not inferred | Role |
|------------------------|------|
| `cats.data.NonEmptyChainImpl.catsNonEmptyChainOps` | Extension ops on `NonEmptyChainImpl.Type[A]` (`map`, `++`, `:+`, `combineAll`, `toNonEmptyList`, …) |
| `cats.data.NonEmptyMapImpl.catsNonEmptyMapOps` | Extension ops on `NonEmptyMapImpl.Type[…]` (`mapKeys`, `map`, `\|+\|`, apply syntax) |
| `cats.kernel.Semigroup[cats.data.NonEmptyChainImpl.Type[A]]` | `Validated` tuple `mapN` when errors use the impl alias |

---

## 2. fs2-data-csv — `org.gnieh::fs2-data-csv-generic`

**Consumers:** lichess-org/lila-search, http4s/http4s-fs2-data (apps can also rename local `csv` packages)

| Missing / not inferred |
|------------------------|
| `fs2.data.csv.generic.internal.Names[A]` (for `fs2.data.csv.generic.semiauto.deriveCsvRowEncoder` / `deriveCsvRowDecoder`) |

---

## 3. scalacheck-derived — `io.github.martinhh::scalacheck-derived`

**Consumers:** martinhh/scalacheck-derived, theiterators/kebs (kebs may be fixed if this is fixed)

| Missing / not inferred |
|------------------------|
| Inline companion summoning in derivation (`summonAll[Tuple.Map[..., io.github.martinhh.deriving.Summoner]]`, `ArbitraryDeriving` / `SumInstanceSummoner` path) |
| `org.scalacheck.Arbitrary[A]` / `Cogen` / `Shrink` via inline `deriveArbitrary` on recursive sums |

---

## 4. glass — `tf.tofu::glass-macro` (+ `glass-core`)

**Consumer:** danslapman/morphling

| Missing / not inferred |
|------------------------|
| `glass.macros.internal.CompanionClass[(T : T.type)]` (required by `glass.macros.DeriveContains` extension wiring) |

---

## 5. gallia-core — `io.github.galliaproject::gallia-core`

**Consumers:** galliaproject/gallia-core, galliaproject/gallia-spark

| Missing / not inferred |
|------------------------|
| `gallia.domain.KVE` tuple→entry implicits (e.g. `KVE.toValueEntryS` / `toValueEntry*` with `(String, Int)`, `(Symbol, String)`, …) |
| `gallia.WTT[T]` (context bound on those KVE conversions; cascades as “No given instance of type gallia.WTT[…]”) |

---

## 6. medeia — `de.megaera::medeia`

| Missing / not inferred |
|------------------------|
| Coproduct-path `BsonDocumentCodec` / encoder/decoder givens for nested sums (compiler picks product `Mirror.Product` instead of coproduct `Mirror.Sum`; fails `summon[scala.deriving.Mirror.Product of …]` on enums/sealed traits inside outer classes) |

---

## 7. prometheus4cats — `com.permutive::prometheus4cats`

| Missing / not inferred |
|------------------------|
| `prometheus4cats.internal.InitLast.Aux[L, R, C]` (chained `.label[…].label[…]` DSL) |
| `prometheus4cats.internal.InitLast.base` (companion entry point hinted by compiler) |

---

## 8. caliban — `com.github.ghostdogpr::caliban`

**Consumer:** polyvariant/smithy4s-caliban

| Missing / not inferred |
|------------------------|
| `caliban.interop.circe.IsCirceDecoder[F]` |
| `caliban.interop.circe.IsCirceEncoder[F]` (orphan-marker wiring for `io.circe.Encoder[caliban.ResponseValue]` and `EncoderOps.asJson`) |

---

## 9. extras-render — `io.kevinlee::extras` (extras-render module)

| Missing / not inferred |
|------------------------|
| `extras.render.Render.CatsContravariant[F]` (optional-deps marker for `given [F[_[_]]: CatsContravariant]: F[extras.render.Render]` → `cats.Contravariant[extras.render.Render]`) |

---

## 10. logger-f — `io.kevinlee::logger-f-core` / `logger-f-cats`

| Missing / not inferred |
|------------------------|
| `loggerf.instances.cats.CatsMonad[M]` (OrphanCats optional-dependency marker for conditional `given logF[F, M: CatsMonad]: Log[F]`) |
| Resulting missing instance: `loggerf.core.Log[scala.concurrent.Future]` (and other cats-backed `F`) |

---

## 11. sbt/io — `sbt/io` (sbt module)

| Missing / not inferred |
|------------------------|
| `sbt.internal.nio.TimeSource.default` (`implicit object default` in `TimeSource` companion; needed for `FileEventMonitor.antiEntropy`, `Creation.apply`, `Update.apply`, `Deletion.apply` cross-package) |

---

## 12. snowplow-scala-analytics-sdk

| Missing / not inferred |
|------------------------|
| `com.snowplowanalytics.snowplow.analytics.scalasdk.decode.ValueDecoder[T]` (e.g. `ValueDecoder.stringOptionColumnDecoder` for `ValueDecoder[Option[String]]`) |
| `RowDecoder.DeriveRowDecoder` / `summonInline` in `RowDecoderCompanion` at parent `scalasdk` call site |

---

## 13. brush — `brush` (ranyitz/brush)

| Missing / not inferred |
|------------------------|
| `brush.Color.RGBColor` → `brush.Color[(Int, Int, Int)]` |
| `brush.Color.StringColor` → `brush.Color[String]` (private `Color` companion givens not visible from `example` package) |

---

## 14. avro4s — `com.sksamuel::avro4s` (`avro4s-cats` module) — conditional

Same root as **cats**; if cats does not re-export ops, **avro4s-cats** needs an explicit import of `cats.data.NonEmptyChainImpl.catsNonEmptyChainOps` in its `Encoder[NonEmptyChain[T]]` definition.

---

## Summary

| Category | Count |
|----------|-------|
| OSS libraries that likely need a library-side change | **14** (13 if avro4s is fixed only via cats) |
| Community-build projects in this bundle | 20 |
| Projects fixed primarily by a cats change | 5–6 (C4 cluster + possibly avro4s) |

**Not separate library fixes:** **lila-search** and **http4s-fs2-data** are application repos; they fail on **fs2-data** plus local package shadowing (`csv`). **kebs** likely follows **scalacheck-derived** if that is fixed.

**Workarounds without library releases:** compiler revert of #25367, or per-call-site explicit imports (e.g. `TimeSource.default`, `Color.RGBColor`, `InitLast.base`).

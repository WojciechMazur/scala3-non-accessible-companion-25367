# snowplow/snowplow-scala-analytics-sdk — summonInline nested-package implicits (cluster C3)

## Original failure

- **Project**: `snowplow/snowplow-scala-analytics-sdk` (v3.2.2)
- **Module**: `snowplow-scala-analytics-sdk`
- **File**: `src/main/scala/com.snowplowanalytics.snowplow.analytics.scalasdk/Event.scala:271`
- **Trigger**: `Parser.deriveFor[Event].get(...)` — inline `RowDecoder.DeriveRowDecoder.of[Event]` over 131 TSV columns

## Error

```
[E172] Type Error: Event.scala:271:4
No given instance of type RowDecoderCompanion_this.DeriveRowDecoder[mirror$proxy1.MirroredElemTypes] was found.
...
But no implicit values were found that match type
  com.snowplowanalytics.snowplow.analytics.scalasdk.decode.ValueDecoder[Option[String]].

The following import might fix the problem:
  import com.snowplowanalytics.snowplow.analytics.scalasdk.decode.ValueDecoder.stringOptionColumnDecoder

Inline stack trace
  RowDecoderCompanion.scala:37  summonInline[DeriveRowDecoder[m.MirroredElemTypes]]
  Parser.scala:70               RowDecoder.DeriveRowDecoder.of[A].get(...)
```

`summonInline` expansion at the parent `scalasdk` call site fails to resolve `ValueDecoder` givens that live in the nested `decode` package companion.

## Reproducer

```bash
scala-cli compile --server=false test.scala -S 3.8.4
scala-cli compile --server=false test.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Passes on 3.8.4; fails on nightly with E172 (minimal trigger is a 1-field `Event` case class).

## Cluster

**C3** — `summonInline` tuple derivation at a parent-package inline site cannot see companion implicits defined in a nested child package (`decode.ValueDecoder`).

## Community-build boundary

```bash
cd repro/snowplow/snowplow-scala-analytics-sdk
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh snowplow/snowplow-scala-analytics-sdk 3.8.4          # compile ok
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh snowplow/snowplow-scala-analytics-sdk 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY  # compile failed
```

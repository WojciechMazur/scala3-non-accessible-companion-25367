# sky-uk/kafka-topic-loader — cats NonEmptyChain extension methods (cluster C3)

## Original failure

- **Project**: `sky-uk/kafka-topic-loader` (v2.2.0)
- **Module**: `kafka-topic-loader`
- **File**: `src/main/scala/uk/sky/kafka/topicloader/config/topicLoaderConfig.scala:53`
- **Trigger**: `Validated.Invalid(e)` pattern match on `ValidatedNec[ConfigException, Config]`; `e.toNonEmptyList` not resolved when error type is `NonEmptyChainImpl.Type`.

Full project also reports `[E172]` on `(idleTimeout, bufferSize, clientId).mapN(...)` (missing `Semigroup[NonEmptyChainImpl.Type[ConfigException]]` for `mapN`).

## Error

```
[E008] Not Found Error: topicLoaderConfig.scala:53:39
value toNonEmptyList is not a member of cats.data.NonEmptyChainImpl.Type[com.typesafe.config.ConfigException], but could be made available as an extension method.

[E172] Type Error: topicLoaderConfig.scala:46 (mapN)
But no implicit values were found that match type cats.kernel.Semigroup[cats.data.NonEmptyChainImpl.Type[com.typesafe.config.ConfigException]].
```

## Reproducer

```bash
scala-cli compile --server=false test.scala -S 3.8.4
scala-cli compile --server=false test.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Passes on 3.8.4; fails on nightly with E008 on `toNonEmptyList`.

## Dependency-free repro

Package-object `ValidatedNec` alias + stub `Validated`/`NonEmptyChain` (`cats/data/package.scala`, `cats/data/nonemptychain.scala`).

```bash
scala-cli compile --server=false test-nodep.scala -S 3.8.4
scala-cli compile --server=false test-nodep.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Passes on 3.8.4; fails on nightly with the same E008 (`toNonEmptyList` on `NonEmptyChainImpl.Type` in `Validated.Invalid` bind).

## Cluster

**C3** — `ValidatedNec` / `NonEmptyChain` de-aliases to `NonEmptyChainImpl.Type` in pattern bindings; cats extension methods and kernel instances not resolved.

Canonical repro: [noelwelsh/mads/test.scala](../noelwelsh/mads/test.scala).

## Community-build boundary

```bash
cd repro/sky-uk/kafka-topic-loader
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh sky-uk/kafka-topic-loader 3.8.4          # compile ok
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh sky-uk/kafka-topic-loader 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY  # compile failed (2× E008/E172)
```

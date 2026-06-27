# Non-accessible companion implicits — community-build regressions

Scala 3 compiler regression bisected to commit **`5321891783b8d432f0a25d9cdf0be01ed444194d`** ([#25367](https://github.com/scala/scala3/pull/25367) — don't infer implicits from non-accessible companion).

Sourced from [scala/community-build3](https://github.com/scala/community-build3) OpenCB nightly compare `b34a019` → `20f6657`.

## Bisect summary

| | |
|---|---|
| **Commit** | `5321891783b8d432f0a25d9cdf0be01ed444194d` |
| **PR** | [#25367](https://github.com/scala/scala3/pull/25367) |
| **Author** | Lukas Rytz |
| **Date** | Fri Jun 12 13:20:35 2026 +0200 |
| **Last good release** | `3.10.0-RC1-bin-20260609-b34a019-NIGHTLY` |
| **First bad release** | `3.10.0-RC1-bin-20260616-4733954-NIGHTLY` |
| **Related issue** | [scala/scala3#25347](https://github.com/scala/scala3/issues/25347) |

**20 community-build projects** (clusters C2–C11). Distinct error *signatures* at compile time; same bisected commit.

## Verify a repro

**Library deps** (`test.scala`):

```bash
cd <project-key>
scala-cli compile --server=false test.scala -S 3.8.4
scala-cli compile --server=false test.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

**Dependency-free** (`test-nodep.scala` where available):

```bash
cd <project-key>
scala-cli compile --server=false test-nodep.scala -S 3.8.4
scala-cli compile --server=false test-nodep.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

**Jar workflow** (C2/C3 — `lichess-org/lila-search`, `http4s/http4s-fs2-data`, `martinhh/scalacheck-derived`, `theiterators/kebs`):

```bash
scala-cli compile --server=false test-nodep-lib.scala -S 3.8.4 -d .nodep-out
(cd .nodep-out && jar cf ../.nodep-lib.jar .)
scala-cli compile --server=false test-nodep.scala -S 3.8.4 --extra-jars .nodep-lib.jar
scala-cli compile --server=false test-nodep.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY --extra-jars .nodep-lib.jar
```

See per-project `notes.md` for stubs, jar steps, and error excerpts.

## Index

| Project | Cluster | Canonical file |
|---------|---------|----------------|
| lichess-org/lila-search | C2 | `test-nodep.scala` |
| http4s/http4s-fs2-data | C2 | `test-nodep.scala` |
| martinhh/scalacheck-derived | C3 | `test-nodep.scala` |
| theiterators/kebs | C3 | `test-nodep.scala` |
| sksamuel/avro4s | C4 | `test-nodep.scala` |
| koterpillar/refinery | C4 | `test-nodep.scala` |
| noelwelsh/mads | C4 | `test-nodep.scala` |
| sky-uk/kafka-topic-loader | C4 | `test-nodep.scala` |
| hireproof/screening | C4 | `test-nodep.scala` |
| sbt/io | C5 | `test-nodep.scala` |
| ranyitz/brush | C5 | `test-nodep.scala` |
| snowplow/snowplow-scala-analytics-sdk | C5 | `test-nodep.scala` |
| galliaproject/gallia-core | C6 | `test.scala` |
| galliaproject/gallia-spark | C6 | `test.scala` |
| danslapman/morphling | C7 | `test-nodep.scala` |
| kevin-lee/extras | C8 | `test-nodep.scala` |
| kevin-lee/logger-f | C8 | `test-nodep.scala` |
| medeia/medeia | C9 | `test.scala` |
| permutive-engineering/prometheus4cats | C10 | `test.scala` |
| polyvariant/smithy4s-caliban | C11 | `test-nodep.scala` |

## Docs

- [BISECT.md](BISECT.md) — release boundaries and first bad commit
- [TRIAGE.md](TRIAGE.md) — error-signature clusters C2–C11 and per-project notes
- [LIBRARY-MITIGATION.md](LIBRARY-MITIGATION.md) — OSS libraries and implicits that need library-side fixes

Bisect logs (`bisect-logs/`) are excluded from this repo; regenerate locally with the command in [BISECT.md](BISECT.md).

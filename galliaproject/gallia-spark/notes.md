# galliaproject/gallia-spark — bobj KVE tuple conversion (not C1)

## Original failure

- **Project**: `galliaproject/gallia-spark` v0.6.1
- **Module**: `gallia-spark` (test-compile)
- **Files**: `SparkCsvTest.scala:24`, `SparkLinesTest.scala:25`, `SparkRddDirectlyTest.scala:27`, …
- **Trigger**: `bobj("foo" -> "BAR1", "baz" -> "1")` and `bobj(_line -> "FOO,BAZ")` in test assertions

## Error

```
[E007] Type Mismatch Error: SparkCsvTest.scala:24:15
Found:    (String, String)
Required: gallia.domain.KVE
```

Same for `(Symbol, String)` via `_line -> …`. Main compile succeeds; `test-compile` fails (15 errors).

## Reproducer

```bash
scala-cli compile --server=false test.scala -S 3.8.4
scala-cli compile --server=false test.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Passes on 3.8.4; fails on nightly with E007. Uses `gallia-core` 0.6.1 (`KVE.toValueEntry*` implicit defs with `[T: WTT]`).

## Dependency-free reproducer

Same stub layout as `gallia-core` (`test-nodep.scala`, `wtt-lib.scala`, `kve-lib.scala`).

```bash
scala-cli compile --server=false test-nodep.scala -S 3.8.4
scala-cli compile --server=false test-nodep.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Status: **partial** — passes both versions; use `test.scala` for nightly E007.

## Cluster

**C3** — `(Key, T)` / `(String, T)` tuples no longer convert to a context-bound target type (`KVE`) when passed to `bobj(...)` varargs. Not C1 (no `Factory` / `IArrayFactory` E172).

## Community-build boundary

```bash
cd repro/galliaproject/gallia-spark
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh galliaproject/gallia-spark 3.8.4          # test-compile ok (tests fail at runtime on JDK 25)
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh galliaproject/gallia-spark 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY  # test-compile failed
```

# sbt/io — TimeSource given not found (not C1)

## Original failure

- **Project**: `sbt/io` (sbt/io module)
- **Module**: `io`
- **Files** (first hits):
  - `sbt/internal/io/SourceModificationWatch.scala:59` — `FileEventMonitor.antiEntropy(...)`
  - `sbt/internal/nio/FileCache.scala:46` — `Creation(p, a)` (and `Update` / `Deletion` at nearby lines)
  - `sbt/internal/nio/FileTreeRepositoryImpl.scala:58` — same `Creation` / `Update` / `Deletion` pattern
- **Trigger**: companion `apply` / `antiEntropy` with `(implicit timeSource: TimeSource)` called from another package without importing `TimeSource.default`; `TimeSource.default` is `implicit object default` in `sbt.internal.nio`.

## Error

```
[E172] Type Error: ...SourceModificationWatch.scala:59:9
No given instance of type sbt.internal.nio.TimeSource was found for parameter timeSource of method antiEntropy in object FileEventMonitor

The following import might fix the problem:
  import sbt.internal.nio.TimeSource.default
```

Same message for `Creation` / `Update` / `Deletion` `apply` in `FileCache.scala` and `FileTreeRepositoryImpl.scala` (13 errors total on bad nightly).

## Community-build confirmation

```bash
cd repro/sbt/io
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh sbt/io 3.8.4
# compile ok; 3 unrelated test failures

SKIP_BUILD_SETUP=1 ../../../scripts/run.sh sbt/io 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
# compile failed (13 E172)
```

## Reproducer

```bash
scala-cli compile --server=false test.scala -S 3.8.4
scala-cli compile --server=false test.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Passes on 3.8.4; fails on nightly with E172 (missing given for `TimeSource` on cross-package `Creation.apply` call).

Minimal shape: `private[nio] implicit object default`, nested `Creation.apply(...)(implicit timeSource: TimeSource)`, call site in `package sbt.internal.io` importing `FileEvent.Creation`.

## Cluster

**Not C1** — E172 missing given for `implicit object` companion default when implicit parameter is resolved from a different package (not Factory / IArrayFactory ambiguity).

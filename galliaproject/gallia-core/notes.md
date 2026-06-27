# galliaproject/gallia-core — bobj KVE tuple conversion (not C1)

## Original failure

- **Project**: `galliaproject/gallia-core` v0.6.1
- **Module**: `gallia-core` (test-compile; main compile ok)
- **File**: `core/src/test/scala/galliatesting/TestDataO.scala:11`
- **Trigger**: `bobj(g -> 1)` and the same `(SKey, T) -> KVE` path across test suite (`bobj(f -> foo)`, `_.transform(...).using(...)`, …)

## Error

```
[E007] Type Mismatch Error: TestDataO.scala:11:24
Found:    (String, Int)
Required: gallia.domain.KVE
```

691 test-compile errors; many secondary `[E172]` on missing `gallia.WTT[T]` (e.g. `JsonTest1.scala:37` — `No given instance of type gallia.WTT[BigInt]`).

## Reproducer

```bash
scala-cli compile --server=false test.scala -S 3.8.4
scala-cli compile --server=false test.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Passes on 3.8.4; fails on nightly with E007. Uses `gallia-core` 0.6.1 (`KVE.toValueEntry*` implicit defs with `[T: WTT]`).

## Dependency-free reproducer

`test-nodep.scala` + `wtt-lib.scala` + `kve-lib.scala` stub `WTT`, `KVE.toValueEntryS`, and `bobj`.

```bash
scala-cli compile --server=false test-nodep.scala -S 3.8.4
scala-cli compile --server=false test-nodep.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Status: **partial** — passes on both 3.8.4 and nightly. The regression depends on `(String, T) → KVE` implicits in precompiled `gallia-core` 0.6.1; this inlined stub does not reproduce the nightly E007 boundary. Use `test.scala` for the authoritative bad compile.

## Cluster

**C3** — `(Key, T)` / `(String, T)` tuples no longer convert to a context-bound target type (`KVE`) when passed to `bobj(...)` varargs. Not C1 (no `Factory` / `IArrayFactory` E172).

## Community-build boundary

```bash
cd repro/galliaproject/gallia-core
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh galliaproject/gallia-core 3.8.4          # test-compile ok
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh galliaproject/gallia-core 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY  # test-compile failed (691 errors)
```

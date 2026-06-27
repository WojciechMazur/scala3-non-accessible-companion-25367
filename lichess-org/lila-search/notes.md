# lichess-org/lila-search — fs2-data-csv Names derivation (cluster C2)

## Original failure

- **Project**: `lichess-org/lila-search` (HEAD)
- **Module**: `lila-game-export`
- **File**: `modules/lila-game-export/src/main/scala/GameCsv.scala:34`
- **Trigger**: `given CsvRowEncoder[GameCsv, String] = deriveCsvRowEncoder` in package `lila.search.game.csv`

## Error

```
[E172] Type Error: GameCsv.scala:34:60
No given instance of type fs2.data.csv.generic.internal.Names[lila.search.game.csv².GameCsv] was found for parameter naming of method deriveCsvRowEncoder in object semiauto

where:    csv  is a package in package fs2.data
          csv² is a package in package lila.search.game
```

Package segment `csv` shadows `fs2.data.csv`; `Names` derivation fails on nightly.

## Reproducer (library-dependent)

```bash
scala-cli compile --server=false test.scala -S 3.8.4
scala-cli compile --server=false test.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Passes on 3.8.4; fails on nightly with E172.

## Dependency-free reproducer

Inlined `Names` + `semiauto.deriveCsvRowEncoder` from fs2-data-csv-generic 1.14.0 in `test-nodep-lib.scala`. The regression requires **separate compilation** of the library stub (same as the Maven artifact boundary); a single-file compile does not reproduce it.

```bash
cd repro/lichess-org/lila-search
scala-cli compile --server=false test-nodep-lib.scala -S 3.8.4 -d .nodep-out
(cd .nodep-out && jar cf ../.nodep-lib.jar .)
scala-cli compile --server=false test-nodep.scala -S 3.8.4 --extra-jars .nodep-lib.jar
scala-cli compile --server=false test-nodep.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY --extra-jars .nodep-lib.jar
```

- `test-nodep-lib.scala`: inlined fs2-data `Names` / `semiauto` (+ shapeless3 for `Labelling` / `Annotations` / `K0`)
- `test-nodep.scala`: trigger only (`example.game.csv` package shadowing)

Passes on 3.8.4; fails on nightly with the same E172 `Names` error.

## Cluster

**C2** — final package segment `csv` + `import fs2.data.csv.*` breaks `deriveCsvRowEncoder` (`Names` given not found).

## Community-build boundary

```bash
cd repro/lichess-org/lila-search
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh lichess-org/lila-search 3.8.4          # lila-game-export compile ok
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh lichess-org/lila-search 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY  # lila-game-export compile failed
```

Note: full project also has unrelated e2e test failures on both versions.

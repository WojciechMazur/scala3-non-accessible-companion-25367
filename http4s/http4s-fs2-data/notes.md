# http4s/http4s-fs2-data — fs2-data-csv Names derivation (cluster C2)

## Original failure

- **Project**: `http4s/http4s-fs2-data` (v0.4.0)
- **Module**: `http4s-fs2-data-csv`
- **File**: `csv/src/test/scala/org/http4s/fs2data/csv/CsvSuite.scala:36-37`
- **Trigger**: `semiauto.deriveCsvRowEncoder` / `deriveCsvRowDecoder` in package `org.http4s.fs2data.csv`

## Error

```
[E172] Type Error: CsvSuite.scala:36:84
No given instance of type fs2.data.csv.generic.internal.Names[CsvSuite.this.Data] was found for parameter naming of method deriveCsvRowEncoder in object semiauto
```

Package segment `csv` shadows `fs2.data.csv`; `Names` derivation fails on nightly.

## Reproducer (library-dependent)

```bash
scala-cli compile --server=false test.scala -S 3.8.4
scala-cli compile --server=false test.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Passes on 3.8.4; fails on nightly with E172.

## Dependency-free reproducer

Inlined `Names` + `semiauto` from fs2-data-csv-generic 1.10.0 in `test-nodep-lib.scala`. Requires pre-compiling the library stub to jar (see [lichess-org/lila-search/notes.md](../lichess-org/lila-search/notes.md)).

```bash
cd repro/http4s/http4s-fs2-data
scala-cli compile --server=false test-nodep-lib.scala -S 3.8.4 -d .nodep-out
(cd .nodep-out && jar cf ../.nodep-lib.jar .)
scala-cli compile --server=false test-nodep.scala -S 3.8.4 --extra-jars .nodep-lib.jar
scala-cli compile --server=false test-nodep.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY --extra-jars .nodep-lib.jar
```

Passes on 3.8.4; fails on nightly with the same E172 `Names` error.

## Cluster

**C2** — final package segment `csv` + `import fs2.data.csv.*` breaks `deriveCsvRowEncoder` (`Names` given not found).

Canonical repro: [lichess-org/lila-search/test.scala](../lichess-org/lila-search/test.scala).

## Community-build boundary

```bash
cd repro/http4s/http4s-fs2-data
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh http4s/http4s-fs2-data 3.8.4          # csv test-compile ok
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh http4s/http4s-fs2-data 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY  # csv test-compile failed
```

# kevin-lee/logger-f — orphan-cats CatsMonad given (cluster C3)

## Original failure

- **Project**: `kevin-lee/logger-f` v2.9.0
- **Module**: `logger-f-core` / `logger-f-cats` (test-compile)
- **File**: `modules/logger-f-cats/jvm/src/test/scala/loggerf/instances/instancesSpec.scala:69`
- **Trigger**: `def runLog[F[*]: Fx: Log: Monad]: F[Unit]` — context bound `Log[F]` when `cats-core` is on the classpath

`Log` extends `orphan.OrphanCats` and defines `given logF[F[*], M[*[*]]: CatsMonad](...)`. Nightly fails to resolve the optional-dependency `CatsMonad[cats.Monad]` marker, so `Log[Future]` (and other cats-backed `F`) is not found.

## Error

```
[E172] Type Error: test.scala:28:24
  val _ = runLog[Future]
                        ^
  Could not find an implicit Log[scala.concurrent.Future].
  If you add cats library to your project,
  you can automatically get `Log[scala.concurrent.Future]` instance provided by logger-f.
```

Same root cause as `kevin-lee/extras` (cluster C3): conditional HK given keyed on an optional-dependency marker fails on nightly.

## Reproducer

```bash
scala-cli compile --server=false test.scala -S 3.8.4
scala-cli compile --server=false test.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Passes on 3.8.4; fails on nightly with E172.

## Cluster

**C3** — conditional HK given `M[*[*]]: CatsMonad` (orphan-cats optional cats dependency) fails to resolve when summoning `Log[F]`.

Related: [kevin-lee/extras/test.scala](../extras/test.scala) (`Contravariant[Render]` via `CatsContravariant` marker).

## Community-build boundary

```bash
cd repro/kevin-lee/logger-f
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh kevin-lee/logger-f 3.8.4          # success (local)
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh kevin-lee/logger-f 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY  # infra failure locally; dashboard reports compile failure
```

Note: local full CB run can fail on unrelated sbt/plugin infra (`NoClassDefFoundError`, JUnit XML paths) after tests pass; the scala-cli repro above is the authoritative compile regression signal.

## Dependency-free reproducer

`test-nodep.scala` inlines `orphan.OrphanCats`, `loggerf.core.Log`, `effectie.core.Fx`, and minimal `cats.Monad` / `CanLog` stubs.

```bash
scala-cli compile --server=false test-nodep.scala -S 3.8.4
scala-cli compile --server=false test-nodep.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Verified: passes 3.8.4, fails nightly with E172 (same missing `Log[Future]` / `CatsMonad` path).

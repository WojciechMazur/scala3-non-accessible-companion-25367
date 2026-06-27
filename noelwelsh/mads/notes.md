# noelwelsh/mads — cats NonEmptyChain extension methods (cluster C3)

## Original failure

- **Project**: `noelwelsh/mads` (v0.2.0)
- **Module**: `mads`
- **Files**:
  - `Mads.scala:66` — `.rep.map(_.combineAll)`
  - `Suspendable.scala:120` — `accum.result :+ a` inside `case r: Rep[s, a]`
- **Trigger**: enum case `Rep` extends `Suspendable[S, NonEmptyChain[A]]`; extension methods on `NonEmptyChain` are not found when the type is exposed as `cats.data.NonEmptyChainImpl.Type[A]`.

## Error

```
[E008] Not Found Error: Mads.scala:66:13
value combineAll is not a member of cats.data.NonEmptyChainImpl.Type[A], but could be made available as an extension method.

[E008] Not Found Error: Suspendable.scala:120:48
value :+ is not a member of cats.data.NonEmptyChainImpl.Type[a], but could be made available as an extension method.
```

## Reproducer

```bash
scala-cli compile --server=false test.scala -S 3.8.4
scala-cli compile --server=false test.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Passes on 3.8.4; fails on nightly with E008 on `combineAll`.

## Dependency-free repro

Uses the package-object alias split plus enum `Rep` GADT. `combineAll` is stubbed on `NonEmptyChainOps` (not a separate `Reducible` instance) so the nightly de-alias bug reproduces; suggested import differs (`catsNonEmptyChainOps` vs `Reducible.ops`) but the primary E008 on `NonEmptyChainImpl.Type[A]` matches.

```bash
scala-cli compile --server=false test-nodep.scala -S 3.8.4
scala-cli compile --server=false test-nodep.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Passes on 3.8.4; fails on nightly with E008 on `combineAll`.

## Cluster

**C3** — `NonEmptyChain` type alias de-aliases to `NonEmptyChainImpl.Type` through enum GADT cases; cats extension methods (`combineAll`, `:+`) not resolved.

Canonical repro: [noelwelsh/mads/test.scala](test.scala).

## Community-build boundary

```bash
cd repro/noelwelsh/mads
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh noelwelsh/mads 3.8.4          # compile ok
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh noelwelsh/mads 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY  # compile failed (2× E008)
```

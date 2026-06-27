# koterpillar/refinery — NonEmptyChain extension methods (not C1)

## Original failure

- **Project**: `koterpillar/refinery` v0.0.8
- **Module**: `refinery`
- **Files**: `ValidatedC.scala:24`, `package.scala:11,15`
- **Trigger**: `e1 ++ e2`, `errors.map(...)`, `value.map(Error.of[C, E])` on `NonEmptyChain` (via `ValidatedC.Errors` type alias)

## Error

```
[E008] Not Found Error: value ++ is not a member of refinery.ValidatedC.Errors[C, E], but could be made available as an extension method.
  import cats.data.NonEmptyChainImpl.catsNonEmptyChainOps

[E008] Not Found Error: value map is not a member of refinery.ValidatedC.Errors[C, E], but could be made available as an extension method.

[E008] Not Found Error: value map is not a member of cats.data.NonEmptyChainImpl.Type[E], but could be made available as an extension method.
```

## Reproducer

```bash
scala-cli compile --server=false test.scala -S 3.8.4
scala-cli compile --server=false test.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Passes on 3.8.4; fails on nightly with E008 on `++` through a `NonEmptyChain` type alias.

## Dependency-free repro

Stub layout mirrors cats: `type NonEmptyChain[+A] = NonEmptyChainImpl.Type[A]` lives in `cats/data/package.scala` (package object), separate from `NonEmptyChainImpl` + `NonEmptyChainOps` in `cats/data/nonemptychain.scala`.

```bash
scala-cli compile --server=false test-nodep.scala -S 3.8.4
scala-cli compile --server=false test-nodep.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Passes on 3.8.4; fails on nightly with the same E008 (`++` / `map` on `NonEmptyChainImpl.Type` through the `Errors` alias).

## Cluster

**Not C1** — C1 is `Factory` / `arrayFactory` vs `IArrayFactory` ambiguity (E172). This failure is E008 extension-method resolution for `cats.data.NonEmptyChain` through a type alias; assign a new cluster (e.g. C2).

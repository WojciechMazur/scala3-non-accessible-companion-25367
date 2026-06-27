# hireproof/screening — cats NonEmptyMap extensions (cluster C2)

## Original failure

- **Project**: `hireproof/screening` v0.0.17
- **Module**: `screening-core`
- **File**: `modules/core/src/main/scala/io/hireproof/screening/Violations.scala`
- **Trigger**: `NonEmptyMap` ops via `cats.syntax.all._` on a case-class field named `toNem` (`mapKeys`, `map`, `|+|`, apply syntax `toNem(history)`)

## Error

```
[E008] value mapKeys is not a member of cats.data.NonEmptyMapImpl.Type[..., ...], but could be made available as an extension method.
       The following import might fix the problem:
         import cats.data.NonEmptyMapImpl.catsNonEmptyMapOps

[E050] value toNem in class Violations does not take parameters
```

Five errors in `Violations.scala`; cascading from extension/apply resolution on `NonEmptyMap`.

## Reproducer

```bash
scala-cli compile --server=false test.scala -S 3.8.4
scala-cli compile --server=false test.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Passes on 3.8.4; fails on nightly with E008 + E050.

## Dependency-free repro

Stub `NonEmptyMapImpl` via cats `Newtype2` pattern (`cats/data/Newtype2.scala`, `cats/data/package.scala`, `cats/data/nonemptymap.scala`).

```bash
scala-cli compile --server=false test-nodep.scala -S 3.8.4
scala-cli compile --server=false test-nodep.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Passes on 3.8.4; fails on nightly with the same primary E008 (`mapKeys` on `NonEmptyMapImpl.Type`) plus cascading E050 on apply syntax.

Minimal trigger: `cats-core` 2.7.0 + `import cats.syntax.all._` + `NonEmptyMap.one(...).mapKeys(...)` and `nem("key")`. Newer `cats-core` 2.13.0 compiles on the same nightly.

## Cluster

**C2** — legacy cats `NonEmptyMap` extension/apply ops not resolved under Scala 3.10 (E008/E050).

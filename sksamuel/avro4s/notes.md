# sksamuel/avro4s — NonEmptyChain extension method in nested closure

## Original failure

- **Project**: `sksamuel/avro4s` v5.0.15
- **Module**: `avro4s-cats`
- **File**: `avro4s-cats/src/main/scala/com/sksamuel/avro4s/cats/package.scala:36`
- **Trigger**: `Encoder[NonEmptyChain[T]]` encode lambda `{ value => value.map(encode).toNonEmptyList.toList.asJava }`

## Error

```
[E008] Not Found Error: .../package.scala:36:23
36 |      { value => value.map(encode).toNonEmptyList.toList.asJava }
   |                 ^^^^^^^^^
   |value map is not a member of cats.data.NonEmptyChainImpl.Type[T], but could be made available as an extension method.

The following import might fix the problem:

  import cats.data.NonEmptyChainImpl.catsNonEmptyChainOps
```

`NonEmptyList` / `NonEmptyVector` encoders in the same file compile; only `NonEmptyChain.map` fails inside the nested function value.

## Reproducer

```bash
scala-cli compile --server=false test.scala -S 3.8.4
scala-cli compile --server=false test.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Passes on 3.8.4; fails on nightly with E008.

## Dependency-free repro

Same cats newtype split as refinery (`cats/data/package.scala` + `cats/data/nonemptychain.scala` with `toNonEmptyList`).

```bash
scala-cli compile --server=false test-nodep.scala -S 3.8.4
scala-cli compile --server=false test-nodep.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Passes on 3.8.4; fails on nightly with the same E008 (`map` on `NonEmptyChainImpl.Type[T]` inside the nested closure).

## Cluster

**C2** — cats `NonEmptyChain.map` extension not resolved in nested generic closure (`val encode = …; { value => value.map(encode) … }`).

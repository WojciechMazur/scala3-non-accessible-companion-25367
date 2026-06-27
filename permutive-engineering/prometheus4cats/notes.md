# permutive-engineering/prometheus4cats — InitLast given search (cluster C2)

## Original failure

- **Project**: `permutive-engineering/prometheus4cats` v6.0.0-M1
- **Module**: `prometheus4cats` (test-compile)
- **File**: `MetricsFactoryDslTest.scala:52` (and 10 related sites)
- **Trigger**: chained metric DSL `.label[String](...).label[Int](...)` on `MetricFactory.WithCallbacks` builders

## Error

```
[E172] No given instance of type prometheus4cats.internal.InitLast.Aux[String, Int, C] was found for parameter initLast of method apply in class WithCallbacks
```

Compiler hint: `import prometheus4cats.internal.InitLast.base`

Cascading `[E081] Missing parameter type` on `.labels[(Int, BigInteger)](...)` when InitLast inference fails upstream.

## Reproducer

Depends on published `prometheus4cats` 6.0.0-M1 (compiled for Scala 3.3.x); failure is in the consumer on nightly when resolving inherited `InitLast` givens across separate compilation.

```bash
scala-cli compile --server=false test.scala -S 3.8.4
scala-cli compile --server=false test.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Passes on 3.8.4; fails on nightly with E172.

## Dependency-free reproducer

`initlast-lib.scala` (library stub) + `test-nodep.scala` (chained `.label[String].label[Int]` DSL). Build the library jar once, then compile the consumer:

```bash
scala-cli package initlast-lib.scala --library -S 3.8.4 --force --server=false -o initlast-lib.jar
scala-cli compile --server=false --extra-jars initlast-lib.jar test-nodep.scala -S 3.8.4
scala-cli compile --server=false --extra-jars initlast-lib.jar test-nodep.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Status: **partial** — passes both versions with this stub/jar split. The regression requires inherited `InitLast.base` from precompiled `prometheus4cats` 6.0.0-M1 (Scala 3.3.x). Use `test.scala` for nightly E172.

## Cluster

**C2** — chained label DSL needs `InitLast.Aux[T, B, C]` from a separately compiled dependency; nightly no longer finds the inherited `InitLast.base` given.

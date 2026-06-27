# danslapman/morphling — glass DeriveContains CompanionClass (cluster C3)

## Original failure

- **Project**: `danslapman/morphling` v4.0.0
- **Module**: `morphling` (test-compile; same 26 errors in morphling-scalacheck, morphling-tapir, morphling-reactivemongo)
- **File**: `core/src/test/scala-3/morphling/samples/Person.scala:26`
- **Trigger**: `object Person extends DeriveContains { ... Person.name ... }` (glass field selector on companion)

## Error

```
[E008] Not Found Error: Person.scala:26:36
value name is not a member of object morphling.samples.Person.
An extension method was tried, but could not be fully constructed:

    morphling.samples.Person.conversion(
      /* missing */
        summon[glass.macros.internal.CompanionClass[(Person : Person.type)]],
    ???).apply()
```

Same pattern for `Person.birthDate`, `Administrator.department`, `Server.host`, etc.

## Reproducer

```bash
scala-cli compile --server=false test.scala -S 3.8.4
scala-cli compile --server=false test.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Passes on 3.8.4; fails on nightly with E008 (missing `CompanionClass` summon for `DeriveContains` extension).

## Dependency-free reproducer

`test-nodep.scala` pulls inlined glass + quotidian macro stubs (`glass-impl-stub.scala`, `derive-contains-stub.scala`, etc.).

```bash
scala-cli compile --server=false test-nodep.scala -S 3.8.4
scala-cli compile --server=false test-nodep.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Verified: passes 3.8.4, fails nightly with E008 on `Person.name` (missing `CompanionClass[(Person : Person.type)]`).

## Cluster

**C3** — glass `DeriveContains` companion field selectors (`Person.name`) fail when `CompanionClass` given cannot be summoned on nightly.

## Community-build boundary

```bash
cd repro/danslapman/morphling
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh danslapman/morphling 3.8.4          # test-compile ok
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh danslapman/morphling 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY  # test-compile failed (26 E008)
```

Note: main `compile` succeeds on both versions; failure is in test sources using `DeriveContains`.

# kevin-lee/extras — conditional HK Contravariant given (cluster C3)

## Original failure

- **Project**: `kevin-lee/extras` v0.51.0
- **Module**: `extras-render` (test-compile)
- **File**: `modules/extras-render/shared/src/test/scala/extras/render/RenderSpec.scala:221`
- **Trigger**: `cats.Contravariant[Render].contramap(Render[UUID])(_.value)` (and Int/String variants)

## Error

```
[E172] Type Error: RenderSpec.scala:221:59
221 |      val renderId: Render[Id] = cats.Contravariant[Render].contramap(Render[UUID])(_.value)
    |                                                           ^
    |No given instance of type cats.Contravariant[extras.render.Render] was found for parameter instance of method apply in object Contravariant.
    |I found:
    |
    |    extras.render.Render.RenderContravariant[F](/* missing */summon[extras.render.Render.CatsContravariant[F]])
    |
    |But no implicit values were found that match type extras.render.Render.CatsContravariant[F].
```

`Render` exposes `Contravariant[Render]` via a conditional given `given [F[_[_]]: CatsContravariant]: F[Render]` keyed on an optional-dependency marker. Nightly fails to unify `F` with `cats.Contravariant` when summoning through `Contravariant[Render].contramap`.

## Reproducer

```bash
scala-cli compile --server=false test.scala -S 3.8.4
scala-cli compile --server=false test.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Passes on 3.8.4; fails on nightly with E172.

## Dependency-free reproducer

`test-nodep.scala` inlines `Render`, orphan-cats `CatsContravariant`, and a minimal `cats.Contravariant` stub.

```bash
scala-cli compile --server=false test-nodep.scala -S 3.8.4
scala-cli compile --server=false test-nodep.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Verified: passes 3.8.4, fails nightly with E172 (same `CatsContravariant[F]` missing).

## Cluster

**C3** — conditional HK given `F[_[_]]: CatsContravariant` fails to resolve type parameter `F` when summoning `Contravariant[Render]`.

## Community-build boundary

```bash
cd repro/kevin-lee/extras
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh kevin-lee/extras 3.8.4          # compile ok (test-compile ok)
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh kevin-lee/extras 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY  # extrasRenderJVM Test/compile failed
```

Note: main `extras-render` compile succeeds on both versions; only test-compile fails on nightly (3 errors in `RenderSpec.scala`).

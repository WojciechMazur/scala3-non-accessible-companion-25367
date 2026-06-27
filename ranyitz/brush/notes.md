# ranyitz/brush — private companion givens not found (not C1)

## Original failure

- **Project**: `ranyitz/brush` v0.3.0
- **Module**: `brush`
- **File**: `src/main/scala/example/Example.scala:24` (also lines 36, 45)
- **Trigger**: `"text".gradient(...)` / `.bgGradient(...)` with RGB tuples or CSS color strings

## Error

```
[E172] Type Error: Example.scala:24:7
No given instance of type brush.Color[(Int, Int, Int)] was found for parameter color of method gradient in class BrushMethods

The following import might fix the problem:
  import brush.Color.RGBColor
```

Same pattern for `brush.Color[String]` / `StringColor` on string gradients.

## Reproducer

Multi-file repro (failure requires separate compilation units; single-file passes on nightly):

```bash
scala-cli compile --server=false test.scala -S 3.8.4
scala-cli compile --server=false test.scala -S 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY
```

Passes on 3.8.4; fails on nightly with E172.

## Cluster

**Not C1.** C1 is `Factory` / `arrayFactory` vs `IArrayFactory` ambiguity on unconstrained `F[_]`.

This is a distinct E172: `implicit object` givens in a `private object Color` companion are not resolved at a cross-package use site (`example` calling `gradient` via `import brush._`) on nightly, though they work on 3.8.4.

## Community-build boundary

```bash
cd repro/ranyitz/brush
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh ranyitz/brush 3.8.4          # compile ok
SKIP_BUILD_SETUP=1 ../../../scripts/run.sh ranyitz/brush 3.10.0-RC1-bin-20260626-20f6657-NIGHTLY  # compile failed (3 errors in Example.scala)
```

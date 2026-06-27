# Bisect — `5321891` / #25367

Range: `3.8.3-RC1-bin-20260121-bc94528-NIGHTLY...3.10.0-RC1-bin-20260627-e449094-NIGHTLY`

Command:

```bash
cd ../scala3
scala project/scripts/bisect.scala -- --bootstrapped \
  --releases 3.10.0-RC1-bin-20260609-b34a019-NIGHTLY...3.10.0-RC1-bin-20260616-4733954-NIGHTLY \
  <validation-script>
```

## Release boundary

```
Last good release: 3.10.0-RC1-bin-20260609-b34a019-NIGHTLY
First bad release: 3.10.0-RC1-bin-20260616-4733954-NIGHTLY
```

## First bad commit

```
5321891783b8d432f0a25d9cdf0be01ed444194d is the first bad commit
commit 5321891783b8d432f0a25d9cdf0be01ed444194d
Author: Lukas Rytz <lukas.rytz@gmail.com>
Date:   Fri Jun 12 13:20:35 2026 +0200

    Don't infer implicits from non-accessible companion (#25367)
    
    Implicits found in the companion object of a type should not be inferred
    if the companion object is not accessible.
    
    Fixes https://github.com/scala/scala3/issues/25347
```

## Projects (20)

| Project | Cluster | Canonical repro |
|---------|---------|-----------------|
| lichess-org/lila-search | C2 | `test-nodep.scala` |
| http4s/http4s-fs2-data | C2 | `test-nodep.scala` |
| martinhh/scalacheck-derived | C3 | `test-nodep.scala` |
| theiterators/kebs | C3 | `test-nodep.scala` |
| sksamuel/avro4s | C4 | `test-nodep.scala` |
| koterpillar/refinery | C4 | `test-nodep.scala` |
| noelwelsh/mads | C4 | `test-nodep.scala` |
| sky-uk/kafka-topic-loader | C4 | `test-nodep.scala` |
| hireproof/screening | C4 | `test-nodep.scala` |
| sbt/io | C5 | `test-nodep.scala` |
| ranyitz/brush | C5 | `test-nodep.scala` |
| snowplow/snowplow-scala-analytics-sdk | C5 | `test-nodep.scala` |
| galliaproject/gallia-core | C6 | `test.scala` |
| galliaproject/gallia-spark | C6 | `test.scala` |
| danslapman/morphling | C7 | `test-nodep.scala` |
| kevin-lee/extras | C8 | `test-nodep.scala` |
| kevin-lee/logger-f | C8 | `test-nodep.scala` |
| medeia/medeia | C9 | `test.scala` |
| permutive-engineering/prometheus4cats | C10 | `test.scala` |
| polyvariant/smithy4s-caliban | C11 | `test-nodep.scala` |

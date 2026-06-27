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

| Project | Bisect log |
|---------|------------|
| lichess-org/lila-search | [lichess-org-lila-search.log](bisect-logs/lichess-org-lila-search.log) |
| http4s/http4s-fs2-data | [http4s-http4s-fs2-data.log](bisect-logs/http4s-http4s-fs2-data.log) |
| martinhh/scalacheck-derived | [martinhh-scalacheck-derived.log](bisect-logs/martinhh-scalacheck-derived.log) |
| theiterators/kebs | [theiterators-kebs.log](bisect-logs/theiterators-kebs.log) |
| sksamuel/avro4s | [sksamuel-avro4s.log](bisect-logs/sksamuel-avro4s.log) |
| koterpillar/refinery | [koterpillar-refinery.log](bisect-logs/koterpillar-refinery.log) |
| noelwelsh/mads | [noelwelsh-mads.log](bisect-logs/noelwelsh-mads.log) |
| sky-uk/kafka-topic-loader | [sky-uk-kafka-topic-loader.log](bisect-logs/sky-uk-kafka-topic-loader.log) |
| hireproof/screening | [hireproof-screening.log](bisect-logs/hireproof-screening.log) |
| sbt/io | [sbt-io.log](bisect-logs/sbt-io.log) |
| ranyitz/brush | [ranyitz-brush.log](bisect-logs/ranyitz-brush.log) |
| snowplow/snowplow-scala-analytics-sdk | [snowplow-snowplow-scala-analytics-sdk.log](bisect-logs/snowplow-snowplow-scala-analytics-sdk.log) |
| galliaproject/gallia-core | [galliaproject-gallia-core.log](bisect-logs/galliaproject-gallia-core.log) |
| galliaproject/gallia-spark | [galliaproject-gallia-spark.log](bisect-logs/galliaproject-gallia-spark.log) |
| danslapman/morphling | [danslapman-morphling.log](bisect-logs/danslapman-morphling.log) |
| kevin-lee/extras | [kevin-lee-extras.log](bisect-logs/kevin-lee-extras.log) |
| kevin-lee/logger-f | [kevin-lee-logger-f.log](bisect-logs/kevin-lee-logger-f.log) |
| medeia/medeia | [medeia-medeia.log](bisect-logs/medeia-medeia.log) |
| permutive-engineering/prometheus4cats | [permutive-engineering-prometheus4cats.log](bisect-logs/permutive-engineering-prometheus4cats.log) |
| polyvariant/smithy4s-caliban | [polyvariant-smithy4s-caliban.log](bisect-logs/polyvariant-smithy4s-caliban.log) |

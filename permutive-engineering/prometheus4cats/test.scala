//> using scala 3.8.4
//> using dep com.permutive::prometheus4cats:6.0.0-M1
//> using dep org.typelevel::cats-effect:3.6.1

import cats.effect.IO

import prometheus4cats._

@main def test(): Unit =
  val factory = MetricFactory.builder.withPrefix("prefix").noop[IO]
  val _ =
    factory.gauge("test").ofDouble.help("help")
      .label[String]("label1")
      .label[Int]("label2")

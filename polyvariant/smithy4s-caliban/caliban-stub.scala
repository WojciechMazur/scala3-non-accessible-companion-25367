package io.circe:

  trait Encoder[A]:
    def apply(a: A): Json

  class Json

  object Json:
    val Null: Json = new Json

  object Encoder:
    def apply[A](using e: Encoder[A]): Encoder[A] = e

package io.circe.syntax:

  import io.circe.{Encoder, Json}

  extension [A](a: A)(using encoder: Encoder[A]) def asJson: Json = encoder(a)

package caliban.interop.circe:

  private[caliban] trait IsCirceEncoder[F[_]]

  private[caliban] object IsCirceEncoder:
    implicit val isCirceEncoder: IsCirceEncoder[io.circe.Encoder] = null

package caliban:

  sealed trait ResponseValue

  object ResponseValue:
    implicit def circeEncoder[F[_]](using ev: caliban.interop.circe.IsCirceEncoder[F]): F[ResponseValue] =
      new io.circe.Encoder[ResponseValue]:
        def apply(a: ResponseValue): io.circe.Json = io.circe.Json.Null
      .asInstanceOf[F[ResponseValue]]

  object Value:
    case class StringValue(value: String) extends ResponseValue

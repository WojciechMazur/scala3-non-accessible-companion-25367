//> using options -Xmax-inlines:150

import scala.deriving.*
import scala.compiletime.*

package com.snowplowanalytics.snowplow.analytics.scalasdk {

  package decode {
    type DecodedValue[A] = Either[String, A]
    type RowDecodeResult[A] = Either[String, A]

    private[decode] trait ValueDecoder[A] {
      def parse(key: Symbol, value: String): DecodedValue[A]
    }

    private[decode] object ValueDecoder {
      def apply[A](implicit readA: ValueDecoder[A]): ValueDecoder[A] = readA

      implicit final val stringOptionColumnDecoder: ValueDecoder[Option[String]] =
        (_, value) => Right(if value.isEmpty then None else Some(value))
    }

    private[scalasdk] trait RowDecoder[L] { self =>
      def apply(row: List[String]): RowDecodeResult[L]
      def map[B](f: L => B): RowDecoder[B] = row => self(row).map(f)
    }

    private[scalasdk] trait RowDecoderCompanion {

      sealed trait DeriveRowDecoder[L] { self =>
        def get(knownKeys: List[Symbol]): RowDecoder[L]
        def map[B](f: L => B): DeriveRowDecoder[B] = new DeriveRowDecoder[B] {
          def get(knownKeys: List[Symbol]): RowDecoder[B] = self.get(knownKeys).map(f)
        }
      }

      object DeriveRowDecoder {
        inline def of[L](using m: Mirror.ProductOf[L]): DeriveRowDecoder[L] = {
          val instance = summonInline[DeriveRowDecoder[m.MirroredElemTypes]]
          instance.map(tuple => m.fromTuple(tuple))
        }
      }

      implicit def hnilFromRow: DeriveRowDecoder[EmptyTuple] = new DeriveRowDecoder[EmptyTuple] {
        def get(knownKeys: List[Symbol]): RowDecoder[EmptyTuple] = row =>
          if row.isEmpty then Right(EmptyTuple) else Left("too many values")
      }

      implicit def hconsFromRow[H: ValueDecoder, T <: Tuple: DeriveRowDecoder]: DeriveRowDecoder[H *: T] =
        new DeriveRowDecoder[H *: T] {
          def get(knownKeys: List[Symbol]): RowDecoder[H *: T] =
            knownKeys match {
              case key :: tailKeys =>
                val tailDecoder = summon[DeriveRowDecoder[T]].get(tailKeys)
                row =>
                  row match {
                    case h :: t =>
                      for
                        hv <- ValueDecoder[H].parse(key, h)
                        tv <- tailDecoder(t)
                      yield hv *: tv
                    case Nil => Left("not enough values")
                  }
              case Nil => throw IllegalStateException()
            }
        }
    }

    object RowDecoder extends RowDecoderCompanion

    private[scalasdk] object Parser {
      inline def deriveFor[A](using mirror: Mirror.ProductOf[A]): RowDecoder[A] = {
        val knownKeys =
          constValueTuple[mirror.MirroredElemLabels].toArray.map(s => Symbol(s.toString)).toList
        RowDecoder.DeriveRowDecoder.of[A].get(knownKeys)
      }
    }
  }

  case class Event(app_id: Option[String])

  object Event {
    def parser(): decode.RowDecoder[Event] =
      decode.Parser.deriveFor[Event]
  }
}

@main def test(): Unit =
  val _ = com.snowplowanalytics.snowplow.analytics.scalasdk.Event.parser()

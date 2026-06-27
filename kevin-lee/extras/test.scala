//> using dep org.typelevel::cats-core:2.13.0

import scala.annotation.implicitNotFound

trait Render[A]:
  def render(a: A): String

object Render:
  def apply[A: Render]: Render[A] = summon[Render[A]]

  given intRender: Render[Int] = _.toString

  @implicitNotFound(
    "Missing CatsContravariant — add cats-core to enable Contravariant[Render]"
  )
  sealed private[Render] trait CatsContravariant[M[_[_]]]
  private[Render] object CatsContravariant:
    inline given getCatsContravariant: CatsContravariant[cats.Contravariant] =
      null.asInstanceOf[CatsContravariant[cats.Contravariant]]

  given RenderContravariant[F[_[_]]: CatsContravariant]: F[Render] =
    new cats.Contravariant[Render]:
      def contramap[A, B](fa: Render[A])(f: B => A): Render[B] = b => fa.render(f(b))
    .asInstanceOf[F[Render]]

final case class Id(value: Int)

@main def test(): Unit =
  val renderId: Render[Id] = cats.Contravariant[Render].contramap(Render[Int])(_.value)
  val _ = renderId.render(Id(42))

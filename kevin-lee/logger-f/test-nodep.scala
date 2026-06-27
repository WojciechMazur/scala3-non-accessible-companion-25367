import scala.annotation.implicitNotFound
import scala.concurrent.{ExecutionContext, Future}

package cats:
  trait Monad[F[_]]:
    def map[A, B](fa: F[A])(f: A => B): F[B]
    def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]

package orphan:
  trait OrphanCats:
    final protected type CatsMonad[M[*[*]]] = OrphanCats.CatsMonad[M]

  object OrphanCats:
    @implicitNotFound("Missing CatsMonad")
    sealed protected trait CatsMonad[M[*[*]]]
    private[OrphanCats] object CatsMonad:
      inline given getCatsMonad: CatsMonad[cats.Monad] =
        null.asInstanceOf[CatsMonad[cats.Monad]]

package effectie.core:
  trait FxCtor[F[*]]:
    def effectOf[A](a: => A): F[A]
    def pureOf[A](a: A): F[A]

  trait Fx[F[*]] extends FxCtor[F]

  object Fx:
    def apply[F[*]](using fx: Fx[F]): Fx[F] = fx

package loggerf:
  enum LeveledMessage:
    case Info(message: () => String)

  object Ignore

package loggerf.logger:
  trait CanLog:
    def getLogger(level: => Any)(message: => String): Unit

package loggerf.core:
  import effectie.core.*
  import loggerf.*
  import loggerf.logger.CanLog
  import orphan.OrphanCats

  @implicitNotFound("Could not find an implicit Log[F].")
  trait Log[F[*]]:
    given EF: FxCtor[F]
    def canLog: CanLog
    def map0[A, B](fa: F[A])(f: A => B): F[B]
    def flatMap0[A, B](fa: F[A])(f: A => F[B]): F[B]
    def log[A](fa: F[A])(toLeveledMessage: A => LeveledMessage | Ignore.type): F[A] =
      flatMap0(fa) { a =>
        toLeveledMessage(a) match
          case LeveledMessage.Info(message) =>
            flatMap0(EF.effectOf(canLog.getLogger(())(message())))(_ => EF.pureOf(a))
          case Ignore => EF.pureOf(a)
      }

  object Log extends OrphanCats:
    def apply[F[*]: Log]: Log[F] = summon[Log[F]]

    given logF[F[*], M[*[*]]: CatsMonad](using EF0: FxCtor[F], canLog0: CanLog, MF0: M[F]): Log[F] with
      override val EF: FxCtor[F]  = EF0
      override val canLog: CanLog = canLog0
      private val MF: cats.Monad[F] = MF0.asInstanceOf[cats.Monad[F]]
      inline override def map0[A, B](fa: F[A])(f: A => B): F[B]       = MF.map(fa)(f)
      inline override def flatMap0[A, B](fa: F[A])(f: A => F[B]): F[B] = MF.flatMap(fa)(f)

  object syntax:
    object all:
      def info(message: => String): LeveledMessage.Info = LeveledMessage.Info(() => message)

import effectie.core.*
import loggerf.core.*
import loggerf.core.syntax.all.info
import loggerf.logger.CanLog

given ExecutionContext = ExecutionContext.global

given CanLog with
  def getLogger(level: => Any)(message: => String): Unit = ()

given Fx[Future] with
  def effectOf[A](a: => A): Future[A] = Future.successful(a)
  def pureOf[A](a: A): Future[A]     = Future.successful(a)

given cats.Monad[Future] with
  def map[A, B](fa: Future[A])(f: A => B): Future[B]             = fa.map(f)
  def flatMap[A, B](fa: Future[A])(f: A => Future[B]): Future[B] = fa.flatMap(f)

def runLog[F[*]: Fx: Log: cats.Monad]: F[Unit] =
  Log[F].log(Fx[F].effectOf(()))(_ => info("ok"))

@main def test(): Unit =
  given ExecutionContext = ExecutionContext.global
  val _ = runLog[Future]

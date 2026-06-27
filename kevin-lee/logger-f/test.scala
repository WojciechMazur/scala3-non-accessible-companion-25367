//> using dep org.typelevel::cats-core:2.12.0
//> using dep io.kevinlee::effectie-core:2.3.0
//> using dep io.kevinlee::logger-f-core:2.9.0

import cats.Monad
import effectie.core.*
import effectie.instances.future.fx.*
import loggerf.core.*
import loggerf.core.syntax.all.*
import loggerf.logger.CanLog
import scala.concurrent.{ExecutionContext, Future}

given CanLog with
  def debug(message: => String): Unit = ()
  def debug(throwable: Throwable)(message: => String): Unit = ()
  def info(message: => String): Unit = ()
  def info(throwable: Throwable)(message: => String): Unit = ()
  def warn(message: => String): Unit = ()
  def warn(throwable: Throwable)(message: => String): Unit = ()
  def error(message: => String): Unit = ()
  def error(throwable: Throwable)(message: => String): Unit = ()

def runLog[F[*]: Fx: Log: Monad]: F[Unit] =
  Log[F].log(Fx[F].effectOf(()))(_ => info("ok"))

@main def test(): Unit =
  given ExecutionContext = ExecutionContext.global
  val _ = runLog[Future]

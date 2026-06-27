package sbt.internal.nio:

  private[nio] trait TimeSource:
    def now: String

  private[nio] object TimeSource:
    implicit object default extends TimeSource:
      override def now: String = "now"

  private[sbt] object FileEvent:
    private[sbt] abstract case class Creation(value: Int):
      def occurredAt: String

    private[sbt] object Creation:
      def apply(value: Int)(implicit timeSource: TimeSource): Creation =
        new Creation(value) { override def occurredAt = timeSource.now }

package sbt.internal.io:

  import sbt.internal.nio.FileEvent.Creation

  def trigger(): Unit =
    val _ = Creation(1)

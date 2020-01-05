package utils

import scala.concurrent.{ExecutionContext, Future}

class FutureTimeTracker[T](m: => Future[T]) {
  private val start = System.nanoTime()

  def track(message: String)(implicit ec: ExecutionContext): Future[T] = {
    m.andThen {
      case _ =>
        val end = System.nanoTime()
        println(s"$message took: ${toNano(end - start)}")
    }
  }

  private def toNano(time: Long) = time / (1000 * 1000)
}

object FutureTimeTracker {
  def apply[T](m: => Future[T]) = new FutureTimeTracker(m)
}

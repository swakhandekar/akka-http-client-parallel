import akka.http.scaladsl.model.HttpRequest
import models.{Combinable, HttpError}
import play.api.libs.json.Reads

import scala.concurrent.Future

class ParallelHttpClient(simpleHttpClient: HttpClient) {

  import AppGlobals.executionContext

  def parallelRequests[T <: Combinable[T]](requests: Iterable[HttpRequest])(implicit reads: Reads[T]): Future[Either[HttpError, T]] = {
    requests
      .par
      .map(simpleHttpClient.simpleHttpRequest[T])
      .map((res: Future[Either[HttpError, T]]) => res)
      .reduce((a, b) => a.zipWith(b)(combineEithers))
  }

  private def combineEithers[T <: Combinable[T]](a: Either[HttpError, T], b: Either[HttpError, T]): Either[HttpError, T] = {
    a match {
      case Left(errA) => Left(errA)
      case Right(resA) =>
        b match {
          case Left(errB) => Left(errB)
          case Right(resB) => Right(resA.combine(resB))
        }
    }
  }

}

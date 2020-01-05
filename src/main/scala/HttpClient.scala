import akka.http.javadsl.settings.ClientConnectionSettings
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.StatusCodes.Success
import akka.stream.scaladsl.JavaFlowSupport.Sink
import models.HttpError
import play.api.libs.json.{Json, Reads}
import utils.FutureTimeTracker

import scala.concurrent.Future

class HttpClient() {

  import AppGlobals.{executionContext, httpExt, materializer}

  def simpleHttpRequest[T](httpRequest: HttpRequest)(implicit reads: Reads[T]): Future[Either[HttpError, T]] = {
    FutureTimeTracker {
      httpExt.singleRequest(httpRequest)
        .flatMap { response =>
          val eventualString = response.entity.dataBytes.runReduce(_ ++ _).map(_.utf8String)

          response.status match {
            case Success(_) =>
              eventualString.map { res =>
                try {
                  Right(Json.parse(res).as[T])
                }
                catch {
                  case e: Exception => println(res)
                    Left(HttpError(e.getMessage))
                }
              }
            case _ =>
              eventualString.map { err =>
                Left(HttpError(err))
              }
          }
        }
    }
  }.track("single raw request")
}

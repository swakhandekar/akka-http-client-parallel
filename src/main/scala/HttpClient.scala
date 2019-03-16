import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.StatusCodes.Success
import models.HttpError
import play.api.libs.json.{Json, Reads}

import scala.concurrent.Future

class HttpClient() {

  import AppGlobals.{executionContext, httpExt, materializer}

  def simpleHttpRequest[T](httpRequest: HttpRequest)(implicit reads: Reads[T]): Future[Either[HttpError, T]] = {
    httpExt.singleRequest(httpRequest)
      .flatMap { response =>
        val eventualString = response.entity.dataBytes.runReduce(_ ++ _).map(_.utf8String)

        response.status match {
          case Success(_) =>
            eventualString.map { res =>
              Right(Json.parse(res).as[T])
            }
          case _ =>
            eventualString.map { err =>
              Left(HttpError(err))
            }
        }
      }
  }
}

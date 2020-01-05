import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.HttpRequest
import models.{HttpError, Person, PersonList}
import utils.FutureTimeTracker

import scala.util.{Failure, Success}

object App {
  import AppGlobals.executionContext
  def main(args: Array[String]): Unit = {
    val httpRequest = HttpRequest(GET, "http://localhost:8080/data")
    val requests = (1 to 8).map(_ => httpRequest)
    val httpClient = new HttpClient()
    val parallelHttpClient = new ParallelHttpClient(httpClient)

    var input = scala.io.StdIn.readLine()
    while(input != "end") {
      val response = FutureTimeTracker {
        parallelHttpClient.parallelRequests[PersonList](requests)
      }.track("combined parallel requests")

      response.onComplete {
        case Success(result: Either[HttpError, PersonList]) =>
          result match {
            case Left(error) => println(error)
            case Right(person) => println(person)
          }
        case Failure(error: Throwable) =>
          error.printStackTrace()
      }
      input = scala.io.StdIn.readLine()
    }

    AppGlobals.shutDown()
  }
}

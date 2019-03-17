import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.HttpRequest
import models.{HttpError, Person}

import scala.util.{Failure, Success}

object App {
  import AppGlobals.executionContext
  def main(args: Array[String]): Unit = {
    val httpRequest1 = HttpRequest(GET, "http://localhost:8080/data1")
    val httpRequest2 = HttpRequest(GET, "http://localhost:8080/data2")
    val httpClient = new HttpClient()
    val parallelHttpClient = new ParallelHttpClient(httpClient)

    val response = parallelHttpClient.parallelRequests[Person](Iterable(httpRequest1, httpRequest2))

    response.onComplete {
      case Success(result: Either[HttpError, Person]) =>
        result match {
          case Left(error) => println(error)
          case Right(person) => println(person)
        }
      case Failure(error: Throwable) =>
        error.printStackTrace()
    }
  }
}

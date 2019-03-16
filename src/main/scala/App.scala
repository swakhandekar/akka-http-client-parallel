import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.HttpRequest
import models.{HttpError, Person}

import scala.util.{Failure, Success}

object App {
  import AppGlobals.executionContext
  def main(args: Array[String]): Unit = {
    val httpRequest = HttpRequest(GET, "http://localhost:8080/data1")
    val response = new HttpClient().simpleHttpRequest[Person](httpRequest)

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

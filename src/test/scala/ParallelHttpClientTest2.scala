import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.HttpRequest
import models.{Combinable, HttpError}
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar.mock
import org.scalatest.{AsyncWordSpec, BeforeAndAfterEach, DoNotDiscover, Matchers}
import play.api.libs.json.{Json, Reads}

import scala.concurrent.Future

@DoNotDiscover
class ParallelHttpClientTest2 extends AsyncWordSpec with Matchers with BeforeAndAfterEach {

  import ParallelHttpClientTest2._

  "parallelRequests" should {
    "return combined result of all requests" in {
      when(httpClient.simpleHttpRequest(request1))
        .thenReturn(Future(Right(TestModel(Seq("a")))))
      when(httpClient.simpleHttpRequest(request2))
        .thenReturn(Future(Right(TestModel(Seq("b", "c")))))

      val futureResponse = parallelHttpClient.parallelRequests[TestModel](requests)

      futureResponse.map { either =>
        either.right.get shouldBe TestModel(Seq("a", "b", "c"))
      }
    }

    "return error occurred at any request" in {
      when(httpClient.simpleHttpRequest(request1))
        .thenReturn(Future(Right(TestModel(Seq("a")))))
      val error = HttpError("weired error")
      when(httpClient.simpleHttpRequest(request2))
        .thenReturn(Future(Left(error)))

      val futureResponse: Future[Either[HttpError, TestModel]] = parallelHttpClient.parallelRequests[TestModel](requests)

      futureResponse.map { either =>
        either.left.get shouldBe error
      }
    }
  }

  override def beforeEach(): Unit = Mockito.reset(httpClient)
}

object ParallelHttpClientTest2 {
  private val httpClient: HttpClient = mock[HttpClient]
  private val parallelHttpClient: ParallelHttpClient = new ParallelHttpClient(httpClient)
  private val request1: HttpRequest = HttpRequest(GET, "req1")
  private val request2: HttpRequest = HttpRequest(GET, "req2")
  private val requests: Iterable[HttpRequest] = Iterable(request1, request2)

  private case class TestModel(elements: Seq[String]) extends Combinable[TestModel] {
    override def combine(that: TestModel): TestModel = copy(elements = elements ++ that.elements)
  }

  private implicit val testModelReads: Reads[TestModel] = Json.reads[TestModel]
}

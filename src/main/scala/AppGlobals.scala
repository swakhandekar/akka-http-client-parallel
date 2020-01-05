import akka.actor.ActorSystem
import akka.http.scaladsl.{Http, HttpExt}
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext

object AppGlobals {
  private val system: ActorSystem = ActorSystem("parallel-calls")
  implicit val executionContext: ExecutionContext = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()(system)
  val httpExt: HttpExt = Http(system)

  def shutDown(): Unit ={
    system.terminate()
  }
}

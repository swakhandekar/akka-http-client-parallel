import org.scalatest._

import scala.collection.immutable

class ParallelSuite extends Suite with BeforeAndAfterAll {

  override def nestedSuites: immutable.IndexedSeq[Suite] = Vector(new ParallelHttpClientTest(), new ParallelHttpClientTest2)

  override protected def runNestedSuites(args: Args): Status = {
    super.runNestedSuites(args)
  }
}

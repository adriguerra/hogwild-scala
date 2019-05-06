import scala.collection.immutable
import scala.collection.concurrent.{Map, TrieMap}

class Parameters(parametersCount: Int) {

  private val parameters: Map[Int, Float] = {
    val parametersHolder = new TrieMap[Int, Float]()
    // Initialization
    (0 until parametersCount).foreach(index => parametersHolder +=((index, 0.0f)))
    parametersHolder
  }

  // TODO implement
  def read_params(indices: Set[Int]): Map[Int, Float] = {

    null
  }

  def update_params(values: Map[Int, Float]) = {
    val updateThread = new Thread {
      override def run(): Unit = {
        // TODO perform update element wise. Element update must be atomic
        values.foreach(value => parameters.put(value._1, value._2))
      }
    }

    updateThread.run()
  }

}

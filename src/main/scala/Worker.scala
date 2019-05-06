import scala.util.Random

class Worker(dataset: Vector[(Int, (Map[Int, Float], Int))], loss: SVM, batch_size: Int, parameters: Parameters) {
  private val thread = new Thread {
    override def run(): Unit = {

      while (true) {
        val samples = Random.shuffle(dataset).take(batch_size)

        // Get parameters indices with non zero gradient
        val gradient_indices = samples.toSet.flatMap(data => {
          val x_indices = data._2._1.map(_._1)
          x_indices.toSet
        })

        // Get corresponding parameters value
        val parameters_subset = parameters.read_params(gradient_indices)

        // Compute gradient
        val gradients = loss.sgd_subset(samples, parameters_subset)

        parameters.update_params()

      }

    }
  }

  def start() = {
    thread.run()
  }

}

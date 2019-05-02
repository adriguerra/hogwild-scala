import SGD._
import Settings._
import Utils._
import scala.concurrent._
import scala.util.Random

object Main {
  def main(args: Array[String]) = {

    // Begin the set up
    val t1 = System.nanoTime()

    // Load data and split it into train/test
    val data = load_reuters_data(train_path, topics_path, test_paths, "CCAT", true)

    val shuffled = Random.shuffle(List(data))
    val (train_set, test_set) = shuffled.splitAt(math.ceil(data.length*train_proportion))

    // Initialize weights, training_losses and array containing cumulated durations of epochs
    var weights = Vector.fill(D)(0.0)
    var training_losses = Vector.empty[Double]
    var validation_losses = Vector.empty[Double]
    var epoch_durations_cum = Vector.empty[Double]

    while(validation_loss >= 0.3) {
      val service: ExecutorService = Executors.newFixedThreadPool(workers)

      val gradients = Future{
        val sample = Random.shuffle(train_set).take(batch_size).toVector
        val gradients = sgd_subset(sample, wb.value, regParam, D)
      }(service)

    }
  }
}

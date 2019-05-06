import SVM._
import Settings._
import Utils._

import scala.collection.concurrent.TrieMap
import scala.util.Random

object Main {
  def main(args: Array[String]) = {

    // Begin the set up
    val t1 = System.nanoTime()

    // Load data and split it into train/test
    val data = load_reuters_data(train_path, topics_path, test_paths, "CCAT", true)

    val shuffled = Random.shuffle(data)
    val (train_set, test_set) = shuffled.splitAt(math.ceil(data.length*train_proportion).toInt)

    val test_set_length = test_set.length

    // Initialize weights, training_losses and array containing cumulated durations of epochs
    val weights = new TrieMap[Int, Double]()
    (0 until D).foreach(index => weights += ((index, 0f)))

    // Getting set up time
    val load_duration = (System.nanoTime - t1) / 1e9d
    println("Set up duration: " + load_duration)

    val threads =
      for (i <- 1 to workers) yield {
        val thread = new Thread {
          override def run {
            var validation_loss = 1.0
            while (validation_loss >= 0.3) {
              val sample = Random.shuffle(train_set).take(batch_size).toVector
              val gradients = sgd_subset(sample, weights, regParam, D)
              gradients.foreach(g => weights.update(g._1, g._2 - alpha * gradients(g._1)))
              validation_loss = compute_loss(test_set.toVector, weights, regParam) / test_set_length
              println(i + " : " + validation_loss)
            }
          }
        }
        thread.start()
        thread
      }

    threads.foreach(t => t.join())
  }
}

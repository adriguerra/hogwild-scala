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
    (0 until D).foreach(index => weights += ((index, 0d)))

    // Getting set up time
    val load_duration = (System.nanoTime - t1) / 1e9d
    println("Set up duration: " + load_duration)

    var validation_loss = 1.0

    // Thread calculating the validation loss
    val thread_val_loss = new Thread{
      override def run{
        while(validation_loss >= 0.3){
      validation_loss = compute_loss(test_set.toVector, weights.snapshot(), regParam) / test_set_length
        println(validation_loss)}
    }}
    thread_val_loss.start()

    // Threads of workers computing the gradients and updating the weights
    val threads =
      for (i <- 1 to workers) yield {
        val thread = new Thread {
          override def run {
            while(validation_loss >= 0.3){
              val sample = Random.shuffle(train_set).take(batch_size).toVector
              val gradients = sgd_subset(sample, weights.snapshot(), regParam, D)
              val du = gradients.filter(x => x._2!=0).size
              gradients.foreach(g => weights.update(g._1, weights(g._1) - alpha * gradients(g._1)/du))
          }}
        }
        thread.start()
        thread
      }


    threads.foreach(t => t.join())
    thread_val_loss.join()
  }
}

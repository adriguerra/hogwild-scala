import SVM._
import Settings._
import Utils._
import scala.collection.mutable

import scala.collection.concurrent.TrieMap
import scala.util.Random

import java.io.File


object Main {
  def main(args: Array[String]) = {

    // Begin the set up
    val t1 = System.nanoTime()

    // Load data and split it into train/test
    val data = load_reuters_data(train_path, topics_path, test_paths, "CCAT", true)

    val shuffled = Random.shuffle(data).toVector
    val (train_set, test_set) = shuffled.splitAt(math.ceil(data.length*train_proportion).toInt)

    val test_set_length = test_set.length
    val train_set_length = train_set.length

    // Initialize weights, training_losses and array containing cumulated durations of epochs
    val weights = mutable.Map[Int, Float]()
    (0 until D).foreach(index => weights.update(index, 0f))

    // Getting set up time
    val load_duration = (System.nanoTime - t1) / 1e9d
    println("Set up duration: " + load_duration)

    var validation_loss = 1.0

    val timer_start = System.nanoTime()
    // Threads of workers computing the gradients and updating the weights
    val threads =
      for (i <- 1 to workers) yield {
        val thread = new Thread {
          override def run {
            while(!this.isInterrupted){
              val sampledIndices = Utils.sampleWithoutReplacement(train_set_length, batch_size)
              val samples = sampledIndices.map(train_set(_))


              val weights_indices = samples.map(_._2._1.keySet).reduce((x, y) => x.union(y)).toVector
              val weights_state = weights_indices.map(index => index -> weights(index)).toMap

              val gradients = sgd_subset(samples, weights_state, regParam, D)
              //val du = gradients.filter(x => x._2!=0).size
              
              gradients.foreach(g => weights.update(g._1, weights_state(g._1) - alpha * gradients(g._1)))
          }}
        }
        thread.start()
        thread
      }

    // Thread calculating the validation loss
    val thread_val_loss = new Thread{
      override def run{
        while(validation_loss >= 0.3){
          validation_loss = compute_loss(test_set, weights.toMap, regParam) / test_set_length
          println(validation_loss)}
      }}
    thread_val_loss.start()


    thread_val_loss.join()
    val timer_end = (System.nanoTime() - timer_start) / 1e9d

    println("Train duration: " + timer_end)

    threads.foreach(t => t.interrupt())
    threads.foreach(t => t.join())
  }
}



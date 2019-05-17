import SVM._
import Settings._
import Utils._
import scala.collection.mutable

import scala.collection.concurrent.TrieMap
import scala.util.Random

import java.io.File


object Main {

  def aggList (list:List[Double],nb: Int): Unit =
  {
    val average = list.foldLeft((0.0, 1))((acc, i) => ((acc._1 + (i - acc._1) / acc._2), acc._2 + 1))._1
    println("Time for operation " + nb + " : " + average)
  }

  def main(args: Array[String]) = {

    // Begin the set up
    val t1 = System.nanoTime()

    // Load data and split it into train/test
    val data = load_reuters_data(train_path, topics_path, test_paths, "CCAT", true).toVector

    val shuffled = Random.shuffle(data)
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
    //var validation_losses = Vector.empty[Double]

    val timer_start = System.nanoTime()

    // Threads of workers computing the gradients and updating the weights
    val nn = train_set.length
    val t2 = System.nanoTime()

    var Timelist1 = List[Double]()
    var Timelist2 = List[Double]()
    var Timelist3 = List[Double]()
    var Timelist4 = List[Double]()
    var Timelist5 = List[Double]()
    var Timelist6 = List[Double]()

    val threads =
      for (i <- 1 to workers) yield {
        val thread = new Thread {
          override def run {
            while(!this.isInterrupted) {
              val timeloop = System.nanoTime()

              val sampleIndex = Random.nextInt(nn)
              Timelist1 = (System.nanoTime - timeloop) / 1e9d :: Timelist1

              //weights.synchronized {
              val weights_indices = train_set(sampleIndex)._2._1.keySet
              Timelist2 = (System.nanoTime - timeloop) / 1e9d :: Timelist2
              val weights_state = weights_indices.map(index => index -> weights(index)).toMap
              Timelist3 = (System.nanoTime - timeloop) / 1e9d :: Timelist3
                val gradients = sgd_subset(train_set(sampleIndex), weights_state, regParam, D)
                //val du = gradients.filter(x => x._2!=0).size
              Timelist4 = (System.nanoTime - timeloop) / 1e9d :: Timelist4
                gradients.foreach(g => weights.update(g._1, weights_state(g._1) - alpha * gradients(g._1)))
              Timelist5 = (System.nanoTime - timeloop) / 1e9d :: Timelist5
              //}
          }}
        }
        thread.start()
        thread
      }

    // Thread calculating the validation loss
    val thread_val_loss = new Thread{
      override def run{
        while(validation_loss >= 0.18){
          val timeloop2 = System.nanoTime()
          Timelist6 = (System.nanoTime - timeloop2) / 1e9d :: Timelist6
          validation_loss = compute_loss(test_set, weights.toMap, regParam) / test_set_length
          println(validation_loss)
        }
      }}
    thread_val_loss.start()


    thread_val_loss.join()

    val timer_end = (System.nanoTime() - timer_start) / 1e9d
    println("Train duration: " + timer_end)

    aggList (Timelist1,1)
    aggList (Timelist2,2)
    aggList (Timelist3,3)
    aggList (Timelist4,4)
    aggList (Timelist5,5)
    aggList (Timelist5,6)

    threads.foreach(t => t.interrupt())
    threads.foreach(t => t.join())

  }

}



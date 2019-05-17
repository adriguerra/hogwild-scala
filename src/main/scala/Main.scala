import SVM._
import Settings._
import Utils._
import scala.collection.mutable

import scala.collection.concurrent.TrieMap
import scala.util.Random

object Main {
  def main(args: Array[String]) = {

    // Begin the set up
    val t1 = System.nanoTime()

    // Load data and split it into train/test
    val data = load_reuters_data(train_path, topics_path, test_paths, "CCAT", true).toVector

    val shuffled = Random.shuffle(data)
    val (train_set, test_set) = shuffled.splitAt(math.ceil(data.length*train_proportion).toInt)


    val test_set_length = test_set.length

    // Initialize weights, training_losses and array containing cumulated durations of epochs
    //var weights = Map[Int, Double]
    val weights = mutable.Map[Int, Double]()

    //val weights = (0 until D).map(index => ((index, 0d))).toMap
    (0 until D).foreach(index => weights.update(index, 0d))


    //HHHHHHHHHHHHHHHHH
    //val weights = new TrieMap[Int, Double]()
    //(0 until D).foreach(index => weights += ((index, 0d)))

    // Getting set up time
    val load_duration = (System.nanoTime - t1) / 1e9d
    println("Set up duration: " + load_duration)

    var validation_loss = 1.0
    //var validation_losses = Vector.empty[Double]

     //Thread calculating the validation loss
//    val thread_val_loss = new Thread{
//      override def run{
//        while(validation_loss >= 0.3){
//          validation_loss = compute_loss(test_set.toVector, weights, regParam) / test_set_length
//          //validation_losses :+= validation_loss
//        println(validation_loss)}
//        val duration = (System.nanoTime - t1) / 1e9d
//        println("Duration to converge to val oss :"+duration)
//        println("Validation losses vector")
//        //println((validation_losses))
//    }}
//    thread_val_loss.start()

    // Threads of workers computing the gradients and updating the weights
    val nn = train_set.length
    val t2 = System.nanoTime()
    val threads =
      for (i <- 1 to workers) yield {
        val thread = new Thread {
          override def run {
            while(validation_loss >= 0.3){
              val sample = Random.nextInt(nn)
              train_set(sample)
              val gradients = sgd_subset(train_set(sample), weights, regParam, D)
              //val du = gradients.filter(x => x._2 != 0)
              //gradients.foreach(g => weights.update(g._1, weights(g._1) - alpha * gradients(g._1)/du))
              // Update weights
              //weights.map(x => )
              //gradients.filter(x => x._2 != 0).foreach(g => weights += (g._1 -> (weights(g._1) - alpha * gradients(g._1))))

              gradients.foreach(g => weights.update(g._1, weights(g._1) - alpha * gradients(g._1)))


              validation_loss = compute_loss(test_set, weights, regParam) / test_set_length
              println(validation_loss)
          }}
        }
        thread.start()
        thread
      }


    threads.foreach(t => t.join())
    val duration = (System.nanoTime()-t2)/1e9d
    println("Duration " + duration)
    //thread_val_loss.join()
    //println("finished")
  }

}

import Settings._
import Utils._

import scala.collection.concurrent.TrieMap
import scala.util.Random
import SGD._

object Main {
  def main(args: Array[String]) = {

    // Begin the set up
    val t1 = System.nanoTime()

    // Load data and split it into train/test
    val data = load_reuters_data(train_path, topics_path, test_paths, "CCAT", true)

    val shuffled = Random.shuffle(data)
    val (train_set, test_set) = shuffled.splitAt(math.ceil(data.length*train_proportion).toInt)


    // Initialize weights, training_losses and array containing cumulated durations of epochs
    var weights = new TrieMap[Int, Double]()

    var training_losses = Vector.empty[Double]
    var validation_losses = Vector.empty[Double]
    var epoch_durations_cum = Vector.empty[Double]

    // Getting set up time
    val load_duration = (System.nanoTime - t1) / 1e9d
    println("Set up duration: " + load_duration)
//
    // The start of training epochs
    val t2 = System.nanoTime()
    var validation_loss = 1.0

    while(validation_loss >= 0.3) {

        // Compute gradient at each node using accessor
        for (i <- 1 to workers) {
          val thread = new Thread {
            override def run {
              val sample = Random.shuffle(train_set).take(batch_size).toVector
              val gradients = sgd_subset(sample, weights, regParam, D)
              val grad_keys = gradients.keys.toVector
              gradients.foreach(g => weights.update(g._1, g._2 - alpha*gradients(g._1)))
            }
          }
          thread.start

          Thread.sleep(50) // slow the loop down a bit
        }


      // Merge gradients computed at each partitions
//      val gradient = gradients.reduce((x, y) => {
//        val list = x.toList ++ y.toList
//        val merged = list.groupBy ( _._1) .map { case (k,v) => k -> v.map(_._2).sum }
//        merged
//      })

      // Compute gradient at each node using accessor
//      val gradients = access.mapPartitions(it => {
//        val sample = Random.shuffle(b_train_set.value).take(batch_size).toVector
//        val gradients = sgd_subset(sample, wb.value, regParam, D)
//
//        Iterator(gradients)
//      }).collect()

//      // Merge gradients computed at each partitions
//      val gradient = gradients.reduce((x, y) => {
//        val list = x.toList ++ y.toList
//        val merged = list.groupBy ( _._1) .map { case (k,v) => k -> v.map(_._2).sum }
//        merged
//      })
//
//      // Update weights
//      val grad_keys = gradient.keys.toVector
//      weights = ((0 until(D)).zip(weights)).map(x => {
//        if (grad_keys.contains(x._1)) x._2 - gradient(x._1)*alpha
//        else x._2
//      }).toVector
//
//      // Compute validation loss
//      validation_loss = compute_loss(test_collected.toVector, wb.value, regParam)
//
//      //val train_loss = (lossRDD.sum)/ count_train_set
//      validation_loss = (validation_loss)/ count_test_set
//      //training_losses :+= train_loss
//      validation_losses :+= validation_loss
//
//      val epoch_duration = (System.nanoTime - t2) / 1e9d
//      epoch_durations_cum :+= epoch_duration
//
//      //println("Current training loss: " + train_loss)
//      println("Current validation loss: " + validation_loss)
//      println("Epoch duration: " + epoch_duration)
    }
//
//    // Prints of some key observations
//    println("Set up duration: " + load_duration)
//    println("Epoch durations: " + epoch_durations_cum)
//    println("Training losses: " + training_losses)
//    println("Validation losses: " + validation_losses)

  }
}

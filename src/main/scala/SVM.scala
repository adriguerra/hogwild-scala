import scala.collection.concurrent.TrieMap

object SVM {

  def sgd_subset(train_XY: Vector[(Int, (Map[Int, Float], Int))], W: TrieMap[Int, Double], regParam: Double, D: Int) =  {
    /*    Computes stochastic gradient descent for a partition (in memory) */

    val wsub = W
    val grads = train_XY.map(xy => (compute_gradient(xy._2._1, xy._2._2, wsub, regParam, D)))
    grads.reduce((x, y) => {
      val list = x.toList ++ y.toList
      val merged = list.groupBy( _._1).map{ case (k,v) => k -> v.map(_._2).sum }
      merged
    })
  }

  def compute_gradient(xn: Map[Int, Float], yn: Double, wsub: TrieMap[Int, Double], regParam: Double, D: Int) = {
    /* Computes the batch gradient for each datapoint */

    val grad = xn.mapValues(xi => { if(is_support(yn, xn, wsub)) xi * -yn else 0})
    grad.map(x => x._2 + regParam * wsub(x._1)).toVector
    grad
  }

  def is_support(yn: Double, xn: Map[Int, Float], w: TrieMap[Int, Double]) = {
    /* Checks if a datapoint is support */

    yn * xn.map(x => w(x._1) * x._2).sum < 1
  }

  def compute_loss(train_XY: Vector[(Int, (Map[Int, Float], Int))], W: TrieMap[Int, Double], regParam: Double): Double = {
    /* Computes the loss over several points  */

    val wsub = W
    val loss = hinge_loss(train_XY, wsub).sum
    val reg = wsub.values.map(w => w * w).sum * regParam / 2
    reg + loss
  }

  def hinge_loss(XY: Vector[(Int, (Map[Int, Float], Int))], w: TrieMap[Int, Double]) = {
    /* Computes the hinge loss over several points */
    val wsub = w
    XY.map(v => {
      val xy = v._2
      val y = xy._2
      val x = xy._1

      val res = 1 - y * x.map(m => m._2 * wsub(m._1)).sum
      if (res < 0)
        0
      else
        res
    })
  }
}

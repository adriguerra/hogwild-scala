import scala.collection.mutable

object SVM {

  def sgd_subset(train_XY: (Int, (Map[Int, Float], Int)), W: Map[Int, Float], regParam: Float, D: Int) =  {
    /*    Computes stochastic gradient descent for a partition (in memory) */

    val wsub = W
    val grads = compute_gradient(train_XY._2._1, train_XY._2._2, wsub, regParam, D)
    grads

  }

  def compute_gradient(xn: Map[Int, Float], yn: Float, wsub: Map[Int, Float], regParam: Float, D: Int) = {
    /* Computes the batch gradient for each datapoint */

    val grad = xn.mapValues(xi => { if(is_support(yn, xn, wsub)) xi * -yn else 0})
    val du = grad.filter(x => x._2 != 0).size
    grad.map(x => x._2 + regParam * wsub(x._1)/du).toVector
    grad
  }

  def is_support(yn: Double, xn: Map[Int, Float], w: Map[Int, Float]) = {

    /* Checks if a datapoint is support */

    yn * xn.map(x => w(x._1) * x._2).sum < 1
  }


  def compute_loss(train_XY: Vector[(Int, (Map[Int, Float], Int))], W: Map[Int, Float], regParam: Float): Float = {
    /* Computes the loss over several points  */

    val wsub = W
    val loss = hinge_loss(train_XY, wsub).sum
    val reg = wsub.values.map(w => w * w).sum * regParam / 2
    reg + loss
  }


  def hinge_loss(XY: Vector[(Int, (Map[Int, Float], Int))], w: Map[Int, Float]) = {

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

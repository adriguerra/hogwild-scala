object Settings {

  val seed = 42
  val train_proportion = 0.9
  val D = 47237
  val N = 23149
  val workers = 4
  val batch_size = 1
  val alpha = 0.03f / workers/*(100.0f / batch_size)*/
  val regParam = 1e-5f
  val topics_path = "./ressources/data/rcv1-v2.topics.qrels"
  val test_paths = List("/data/datasets/lyrl2004_vectors_test_pt0.dat",
    "/data/datasets/lyrl2004_vectors_test_pt1.dat",
    "/data/datasets/lyrl2004_vectors_test_pt2.dat",
    "/data/datasets/lyrl2004_vectors_test_pt3.dat")
  val train_path = "./ressources/data/lyrl2004_vectors_train.dat"

}
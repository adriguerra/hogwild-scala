object Settings {

  val seed = 42
  val train_proportion = 0.9
  val D = 47237
  val N = 23149
  val workers = 6
  val nb_epochs = 10
  val batch_size = 100
  val alpha = 0.03 * (100.0 / batch_size) / workers
  val regParam = 1e-5
  val topics_path = "/data/datasets/rcv1-v2.topics.qrels"
  val test_paths = List("/data/datasets/lyrl2004_vectors_test_pt0.dat",
    "/data/datasets/lyrl2004_vectors_test_pt1.dat",
    "/data/datasets/lyrl2004_vectors_test_pt2.dat",
    "/data/datasets/lyrl2004_vectors_test_pt3.dat")
  val train_path = "/data/datasets/lyrl2004_vectors_train.dat"

}
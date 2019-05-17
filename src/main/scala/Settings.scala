object Settings {

  val seed = 42
  val train_proportion = 0.9
  val D = 47237
  val N = 23149
  val workers = 4
  val batch_size = 1
  val alpha = 0.05f
  val regParam = 1e-5f
  val topics_path = "/home/julien/data/rcv1-v2.topics.qrels"
  val test_paths = List("/home/julien/data/datasets/lyrl2004_vectors_test_pt0.dat",
    "/home/julien/data/datasets/lyrl2004_vectors_test_pt1.dat",
    "/home/julien/data/datasets/lyrl2004_vectors_test_pt2.dat",
    "/home/julien/data/datasets/lyrl2004_vectors_test_pt3.dat")
  val train_path = "/home/julien/data/lyrl2004_vectors_train.dat"

}
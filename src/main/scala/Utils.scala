import scala.io.Source
import scala.util.Random

object Utils {

  def generate_mappings(datapoint: Array[String]) = {
    /* Generates mappings for each sample datapoint*/
    val d = Map(0 -> 1.0f)
    val mappings = datapoint.map(x => {
      val pairs = x.split(":")
      (pairs.head.toInt, pairs.last.toFloat)
    }).toMap
    d ++ mappings
  }

  def generate_labelled_data(lines: List[String], topics_path: String, selected_cat: String) = {
    /* Generates for each label the corresponding mappings */
    val categories = get_category_dict(topics_path)
    lines.map(line => {
        val elements = line.trim().split(" ")
        val label = elements.head.toInt
        val mappings = generate_mappings(elements.tail.tail)
        val value = categories.get(label) match {
          case Some(value) => if (value.contains(selected_cat)) 1 else - 1
          case None => -1
        }
        (label, (mappings, value))
      })
  }

  def load_sample_reuters_data(train_path: String, topics_path: String, test_paths: List[String], selected_cat: String, train: Boolean) = {
    /* Loads a sample of data to train locally in order to test the implementation */

    if (train) {
      val source = Source.fromFile(train_path).getLines().take(20).toList
      generate_labelled_data(source, topics_path, selected_cat)
    }
    else {
      var res = List[(Int, (Map[Int, Float], Int))]()
      for (path <- test_paths) {
        val source = Source.fromFile(path).getLines().take(20).toList
        res ++= generate_labelled_data(source, topics_path, selected_cat)
      }
      res
    }
  }

  def load_reuters_data(train_path: String, topics_path: String, test_paths: List[String], selected_cat: String, train: Boolean) = {
    /* Loads the desired data*/

    if (train) {
      val source = Source.fromFile(train_path).getLines().toList
      generate_labelled_data(source, topics_path, selected_cat)
    }
    else {
      var res = List[(Int, (Map[Int, Float], Int))]()
      for (path <- test_paths) {
        val source = Source.fromFile(path).getLines().toList
        res ++= generate_labelled_data(source, topics_path, selected_cat)
      }
      res
    }
  }

  def get_category_dict(topics_path: String) = {
    /* Generates the categories of our sample points*/

    val source = Source.fromFile(topics_path)
    val lines = source.getLines().map(line => {
      val s = line.trim().split(" ")
      (s.tail.head.toInt, s.head)
    }).toList
    lines.groupBy(_._1).mapValues(l => l.map(_._2))
  }

  def sampleWithoutReplacement(to: Int, count: Int): Vector[Int] = {
    var samples = Set[Int]()

    while (samples.size < count) {
      samples = samples + Random.nextInt(to)
    }

    return samples.toVector
  }
}


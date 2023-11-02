import org.scalatest.funsuite.AnyFunSuite
import org.slf4j.LoggerFactory
import Main.Main
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.graphx.{Graph, GraphLoader}
import org.apache.spark.rdd.RDD

import scala.collection.mutable.ListBuffer

class attackNode extends AnyFunSuite {
  val logger = LoggerFactory.getLogger(this.getClass)
  logger.info("Checks that random walker function attacks a node")

  test("attacknode") {
    val node = "6"
    val valuable = Array("4", "6", "7")
    val lst = ListBuffer("1", "2", "3")

    val result = Main.shouldIAttack(node, lst, valuable)
    assert(result)
  }
}

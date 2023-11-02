import com.google.common.graph.{MutableValueGraph, ValueGraphBuilder}
import org.scalatest.funsuite.AnyFunSuite
import org.slf4j.LoggerFactory
import Main.Main

import scala.collection.mutable.ListBuffer

class removeNodes extends AnyFunSuite {
  val logger = LoggerFactory.getLogger(this.getClass)
  logger.info("Checks there is three differences when three nodes are removed")
  test("removenodes") {
    val firstShard: MutableValueGraph[String, Int] = ValueGraphBuilder.directed.allowsSelfLoops(false).build
    val secondShard: MutableValueGraph[String, Int] = ValueGraphBuilder.directed.allowsSelfLoops(false).build

    List("1", "2", "3", "4", "5", "6", "7", "8", "9", "10").foreach(node => firstShard.addNode(node))
    List("1", "2", "3", "4", "5", "6", "7").foreach(node => secondShard.addNode(node))

    firstShard.putEdgeValue("1", "2", 1)
    firstShard.putEdgeValue("5", "6", 1)

    secondShard.putEdgeValue("1", "2", 1)
    secondShard.putEdgeValue("5", "6", 1)

    val result = Main.simRank(firstShard, secondShard)
    assert(result == ListBuffer("8", "9", "10"))
  }
}
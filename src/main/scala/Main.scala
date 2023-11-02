package Main

import java.io.{FileInputStream, ObjectInputStream}
import com.typesafe.config.{Config, ConfigFactory}
import com.google.common.graph.{MutableValueGraph, ValueGraphBuilder}
import org.slf4j.{Logger, LoggerFactory}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD

import scala.collection.JavaConverters.iterableAsScalaIterableConverter
import scala.collection.mutable.ListBuffer
import scala.collection.parallel.CollectionConverters.{ImmutableIterableIsParallelizable, ImmutableSeqIsParallelizable, IterableIsParallelizable, seqIsParallelizable}
import scala.util.Random

object Main {
  val random = new Random
  val logger = LoggerFactory.getLogger("main")

  val applicationConf: Config = ConfigFactory.load("application.conf")

  /*
   * Reconstructs a graph of the type MutableValueGraph from the lists of nodes and edges provided.
   * This graph is used for simrank calculations, as was done in homework 1.
   */
  def createGraph(nodesList: Array[String], edgesList: Array[String]): MutableValueGraph[String, Int] = {
    val graph: MutableValueGraph[String, Int] = ValueGraphBuilder.directed.allowsSelfLoops(false).build

    nodesList.foreach(node => graph.addNode(node))
    edgesList.foreach(node => graph.putEdgeValue(node.split(":")(0), node.split(":")(1), 1))

    graph
  }

  /*
   * Determines if a given node is safe to attack or if it is a honeypot.
   * The method checks if the node is contained in the list of modified nodes
   * as determined by the simrank algorithm. If yes, we do not attack. Otherwise,
   * attack!
   */
  def shouldIAttack(node:String, modifiedList:ListBuffer[String], valueable:Array[String]): Boolean = {
    if (modifiedList.contains(node)) {
      logger.info("this node is a honeypot, do not attack")
      false
    }
    else {
      if (valueable.contains(node)) {
        logger.info("Attacked node " + node + "!")
        true
      }
      else {
        logger.info("There is no valuable data here, do not attack")
        false
      }

    }
  }

  /*
   * Performs random walks on the graph from the given starting node. Will continue to walk along
   * randomly determined path until an attack-able node has been found. The input node will be checked
   * for potential attack-ability. If it can be attacked, it will be and the function returns. Else we
   * move onto a randomly-selected neighbor node. Process repeats until a node is attacked.
   */
  def randomWalker(node:String, nodesList:Array[String], modifiedList:ListBuffer[String], valueable:Array[String]): String = {
    val attack = shouldIAttack(node, modifiedList, valueable)

    if (attack) {
      return "Attacked!"
    }
    else {
      val neighbors = new ListBuffer[String]() // create list of neighbors
      nodesList.foreach(x => if (x.split(" ").head == node) neighbors += x.substring(x.lastIndexOf(" ") + 1))
      val nextNode = neighbors(random.nextInt(neighbors.length)).split(" ").head
      randomWalker(nextNode, nodesList, modifiedList, valueable)
    }

    "Attacked!"
  }

  /*
   * Compares original and perturbed graph to determine the delta between the two.
   * Inspired by simrank algorithm of homework 1.
   * Algorithm traverses through the list of nodes of the original graph
   * and compares it to the nodes of the perturbed graph to determine which have been removed.
   * The same is done with the two flipped to determine which nodes have been added.
   * The nodes that have been modified are added to a list, which is returned.
   */
  def simRank(orig: MutableValueGraph[String, Int], perturbed: MutableValueGraph[String, Int]): ListBuffer[String] = {
    val thisNodes = orig.nodes().asScala.toList.par
    val thatNodes = perturbed.nodes().asScala.toList.par

    val lst = new ListBuffer[String]()

    thisNodes.foldLeft(0) {
      case (acc, node) =>
        if (thatNodes.exists(_ == node)) {
          acc
        }
        else {
          lst += node
          acc + 1
        }

    } + thatNodes.foldLeft(0) {
      case (acc, node) => if (thisNodes.exists(_ == node)) {
        acc
      }
      else {
        lst += node
        acc + 1
      }
    }

    logger.info("Modified nodes: " + lst.toString())
    lst
  }


  def main(args: Array[String]) = {
    // get object g which will be used to reconstruct the graphs from NetGameSim
    val fileInputStream = new FileInputStream(applicationConf.getString("app.shardsFile"))
    val objectInputStream = new ObjectInputStream(fileInputStream)
    val g = objectInputStream.readObject()
    objectInputStream.close()

    // Recreate the graphs from the file contents.
    // Contents are formatted as four semi-colon separated lists.
    val firstShard = createGraph(g.toString.split(";")(0).split(","), g.toString.split(";")(2).split(","))
    val secondShard = createGraph(g.toString.split(";")(1).split(","), g.toString.split(";")(3).split(","))

    val valuable = g.toString.split(";")(4).split(",")
    val lst = simRank(firstShard, secondShard)

    val nWalks = applicationConf.getInt("app.numWalks")

    // create a SparkSession
    val conf = new SparkConf().setAppName("RandomWalksApp").setMaster("local[4]")
    val sc = new SparkContext(conf)

    // create graph from file of node->node pairs
    val graph = GraphLoader.edgeListFile(sc, applicationConf.getString("app.graphFile"))

    // create collection of node->node pairs that can be used to get neighbors
    val neighbors: RDD[String] =
      graph.triplets.map(triplet =>
        triplet.srcId + " maps to " + triplet.dstId)

    val arr = neighbors.collect()
    val node = arr(random.nextInt(arr.length)).split(" ").head // initialize start node for random walks

    // run Spark process
    val rdd = sc
      .parallelize(1 to nWalks, nWalks)
      .map{
        f =>randomWalker(node, arr, lst, valuable)
      }
      .collect()

    sc.stop()

    logger.info("Result:")
    rdd.foreach(l => logger.info(l))
  }
}



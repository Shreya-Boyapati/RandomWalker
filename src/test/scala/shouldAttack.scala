import org.scalatest.funsuite.AnyFunSuite
import org.slf4j.LoggerFactory
import Main.Main

import scala.collection.mutable.ListBuffer

class shouldAttack extends AnyFunSuite {
  val logger = LoggerFactory.getLogger(this.getClass)
  logger.info("Checks that honeypot is not attacked")

  test("shouldattack") {
    val node = "1"
    val valueable = Array("5")
    val lst = ListBuffer("1", "2", "3")

    val result = Main.shouldIAttack(node, lst, valueable)
    assert(!result)
  }

}

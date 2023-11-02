package NetGraphAlgebraDefs

trait NetGraphComponent extends Serializable

@SerialVersionUID(123L)
case class NodeObject(param1: Int, param2: Int)

@SerialVersionUID(123L)
case class Action(actionType: Int, fromNode: NodeObject, toNode: NodeObject, fromId: Int, toId: Int, resultingValue: Option[Int], cost: Double)
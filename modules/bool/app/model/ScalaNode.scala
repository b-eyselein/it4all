package model

import scala.collection.JavaConverters._

sealed abstract class ScalaNode {
  def getUsedVariables() = usedVariables asJava

  def evaluate(assignment: Assignment): Boolean
  def negate(): ScalaNode
  def getAsString(needsParans: Boolean): String
  def usedVariables(): Set[Character]
}

case class NotScalaNode(child: ScalaNode) extends ScalaNode {
  override def evaluate(assignment: Assignment) = !child.evaluate(assignment)

  override def negate() = child

  override def getAsString(needsParans: Boolean) = {
    val inner = "NOT " + child.getAsString(needsParans)
    if (needsParans) "(" + inner + ")" else inner
  }
  override def usedVariables = child usedVariables
}

case class Variable(variable: Character) extends ScalaNode {
  def getVariable = variable

  override def evaluate(assignment: Assignment) = assignment get variable

  override def negate() = ScalaNode.not(this)

  override def getAsString(needsParans: Boolean) = variable toString

  override def usedVariables = Set(variable)
}

case class Constant(value: Boolean) extends ScalaNode {

  override def evaluate(assignment: Assignment) = value

  override def negate() = if (value) FALSE else TRUE

  override def getAsString(needsParans: Boolean) = if (value) "1" else "0"

  override def usedVariables = Set.empty
}

object TRUE extends Constant(true)

object FALSE extends Constant(false)

sealed abstract class BinaryScalaNode(operator: String, left: ScalaNode, right: ScalaNode, eval: (Boolean, Boolean) => Boolean) extends ScalaNode {
  override def evaluate(assignment: Assignment) = eval.apply(left.evaluate(assignment), right.evaluate(assignment))

  override def negate() = this match {
    case AndScalaNode(l, r) => ScalaNode.nand(l, r)

    case NAndScalaNode(l, r) => ScalaNode.and(l, r)

    case OrScalaNode(l, r) => ScalaNode.nor(l, r)

    case NOrScalaNode(l, r) => ScalaNode.or(l, r)

    case XOrScalaNode(l, r) => ScalaNode.equiv(l, r)

    case Equivalency(l, r) => ScalaNode.xor(l, r)

    case Implication(l, r) => ScalaNode.and(l, r.negate())
  }

  override def getAsString(needsParans: Boolean) = left + operator + right

  override def usedVariables = left.usedVariables ++ right.usedVariables
}

case class AndScalaNode(l: ScalaNode, r: ScalaNode) extends BinaryScalaNode("AND", l, r, (l, r) => l && r)

case class OrScalaNode(l: ScalaNode, r: ScalaNode) extends BinaryScalaNode("OR", l, r, (l, r) => l || r)

case class NAndScalaNode(l: ScalaNode, r: ScalaNode) extends BinaryScalaNode("NAND", l, r, (l, r) => !(l && r))

case class NOrScalaNode(l: ScalaNode, r: ScalaNode) extends BinaryScalaNode("NOR", l, r, (l, r) => !(l || r))

case class XOrScalaNode(l: ScalaNode, r: ScalaNode) extends BinaryScalaNode("XOR", l, r, (l, r) => l ^ r)

case class Equivalency(l: ScalaNode, r: ScalaNode) extends BinaryScalaNode("EQUIV", l, r, (l, r) => l == r)

case class Implication(l: ScalaNode, r: ScalaNode) extends BinaryScalaNode("IMPL", l, r, (l, r) => !l || r)

object ScalaNode {

  def and(l: ScalaNode, r: ScalaNode) = new AndScalaNode(l, r)

  def or(l: ScalaNode, r: ScalaNode) = new OrScalaNode(l, r)

  def nand(l: ScalaNode, r: ScalaNode) = new NAndScalaNode(l, r)

  def nor(l: ScalaNode, r: ScalaNode) = new NOrScalaNode(l, r)

  def xor(l: ScalaNode, r: ScalaNode) = new XOrScalaNode(l, r)

  def equiv(l: ScalaNode, r: ScalaNode) = new Equivalency(l, r)

  def impl(l: ScalaNode, r: ScalaNode) = new Implication(l, r)

  def not(child: ScalaNode) = new NotScalaNode(child)

  def variable(variable: Character) = new Variable(variable)

  def constant(value: Boolean) = if (value) TRUE else FALSE

  def constant(value: String) = value match {
    case "1" | "true" | "TRUE" => TRUE
    case "0" | "false" | "FALSE" => FALSE
    case x if(x.length == 1) => variable(x charAt 0)
    case _ => throw new CorrectionException(value, "The subformula \"" + value + "\" could not be identified!");
  }

}
package model

import scala.language.implicitConversions

sealed abstract class ScalaNode {
  def evaluate(assignment: Assignment): Boolean
  def negate(): ScalaNode
  def getAsString(needsParans: Boolean): String
  def usedVariables(): Set[Variable]

  def and(that: ScalaNode) = new AndScalaNode(this, that)
  def or(that: ScalaNode) = new OrScalaNode(this, that)
  def nand(that: ScalaNode) = new NAndScalaNode(this, that)
  def nor(that: ScalaNode) = new NOrScalaNode(this, that)
  def xor(that: ScalaNode) = new XOrScalaNode(this, that)
  def impl(that: ScalaNode) = new Implication(this, that)
  def equiv(that: ScalaNode) = new Equivalency(this, that)

  def unary_-() = new NotScalaNode(this)

  override def toString() = getAsString(false)
}

case class NotScalaNode(child: ScalaNode) extends ScalaNode {
  override def evaluate(assignment: Assignment) = !child.evaluate(assignment)

  override def negate() = child

  override def getAsString(needsParans: Boolean) = {
    val inner = "not " + child.getAsString(needsParans)
    if (needsParans) "(" + inner + ")" else inner
  }
  override def usedVariables = child.usedVariables
}

case class Variable(variable: Char) extends ScalaNode with Ordered[Variable] {
  override def evaluate(assignment: Assignment) = assignment(this)

  override def negate() = ScalaNode.not(this)

  override def getAsString(needsParans: Boolean) = variable.toString()

  override def usedVariables = Set(this)

  override def compare(that: Variable) = variable - that.variable
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
    case AndScalaNode(l, r) => l nand r

    case NAndScalaNode(l, r) => l and r

    case OrScalaNode(l, r) => l nor r

    case NOrScalaNode(l, r) => l or r

    case XOrScalaNode(l, r) => l equiv r

    case Equivalency(l, r) => l xor r

    case Implication(l, r) => l and r.negate()
  }

  override def getAsString(needsParans: Boolean) = {
    val inner = left.getAsString(left.isInstanceOf[BinaryScalaNode]) + " " + operator.toLowerCase + " " + right.getAsString(right.isInstanceOf[BinaryScalaNode])
    if (needsParans) "(" + inner + ")" else inner
  }

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

  //  implicit def toScalaNode(char: Char): ScalaNode = toVariable(char)

  implicit def toVariable(char: Char) = new Variable(char)

  def not(child: ScalaNode) = new NotScalaNode(child)

  def unary_-(child: ScalaNode) = new NotScalaNode(child)

  def variable(variable: Character) = new Variable(variable)

  def constant(value: Boolean) = if (value) TRUE else FALSE

  def constant(value: String): ScalaNode = value match {
    case "1" | "true" | "TRUE" => TRUE
    case "0" | "false" | "FALSE" => FALSE
    case x if (x.length == 1) => x charAt 0
    case _ => throw new CorrectionException(value, "The subformula \"" + value + "\" could not be identified!");
  }

}
package model.essentials

import model.essentials.ScalaNode._

sealed abstract class ScalaNode {

  def apply(assignment: BoolAssignment): Boolean

  def negate: ScalaNode

  def getAsString(needsParans: Boolean): String

  def usedVariables: Set[Variable]

  def and(that: ScalaNode) = AndScalaNode(this, that)

  def or(that: ScalaNode) = OrScalaNode(this, that)

  def nand(that: ScalaNode) = NAndScalaNode(this, that)

  def nor(that: ScalaNode) = NOrScalaNode(this, that)

  def xor(that: ScalaNode) = XOrScalaNode(this, that)

  def impl(that: ScalaNode) = Implication(this, that)

  def equiv(that: ScalaNode) = Equivalency(this, that)

  def unary_-() = NotScalaNode(this)

  override def toString: String = getAsString(false)

  def asString: String = getAsString(false)

  def asHtml: String = {
    var formulaAsHtml = getAsString(false)
    for ((key, value) <- HtmlReplacers) formulaAsHtml = formulaAsHtml.replaceAll(key, value)
    formulaAsHtml
  }

}

object ScalaNode {

  val HtmlReplacers = Map(
    "impl" -> "&rArr;",
    "nor" -> "&#x22bd;",
    "nand" -> "&#x22bc;",
    "equiv" -> "&hArr;",
    "not " -> "&not;",
    "and" -> "&and;",
    "xor" -> "&oplus;",
    "or" -> "&or;")

  def not(child: ScalaNode) = NotScalaNode(child)

  def constant(value: Boolean): Constant = if (value) TRUE else FALSE

  def constant(value: String): ScalaNode = value match {
    case "1" | "true" | "TRUE"   => TRUE
    case "0" | "false" | "FALSE" => FALSE
    case x if x.length == 1      => Variable(x charAt 0)
    case _                       => null
  }

}

object FALSE extends Constant(false)

case class NAndScalaNode(l: ScalaNode, r: ScalaNode) extends BinaryScalaNode("NAND", l, r, (l, r) => !(l && r))

case class Implication(l: ScalaNode, r: ScalaNode) extends BinaryScalaNode("IMPL", l, r, (l, r) => !l || r)

case class Equivalency(l: ScalaNode, r: ScalaNode) extends BinaryScalaNode("EQUIV", l, r, (l, r) => l == r)

case class NOrScalaNode(l: ScalaNode, r: ScalaNode) extends BinaryScalaNode("NOR", l, r, (l, r) => !(l || r))

case class NotScalaNode(child: ScalaNode) extends ScalaNode {

  override def apply(assignment: BoolAssignment): Boolean = !child(assignment)

  override def negate: ScalaNode = child

  override def getAsString(needsParans: Boolean): String = {
    val inner = "not " + child.getAsString(needsParans)
    if (needsParans) "(" + inner + ")" else inner
  }

  override def usedVariables: Set[Variable] = child.usedVariables

}

case class OrScalaNode(l: ScalaNode, r: ScalaNode) extends BinaryScalaNode("OR", l, r, (l, r) => l || r)

case class Constant(value: Boolean) extends ScalaNode {

  override def apply(assignment: BoolAssignment): Boolean = value

  override def negate: ScalaNode = if (value) FALSE else TRUE

  override def getAsString(needsParans: Boolean): String = if (value) "1" else "0"

  override def usedVariables: Set[Variable] = Set.empty
}

object TRUE extends Constant(true)

case class Variable(variable: Char) extends ScalaNode with Ordered[Variable] {

  override def apply(assignment: BoolAssignment) = assignment(this)

  override def negate: ScalaNode = not(this)

  override def getAsString(needsParans: Boolean): String = variable.toString

  override def usedVariables = Set(this)

  override def compare(that: Variable): Int = variable - that.variable

}

case class AndScalaNode(l: ScalaNode, r: ScalaNode) extends BinaryScalaNode("AND", l, r, (l, r) => l && r)

case class XOrScalaNode(l: ScalaNode, r: ScalaNode) extends BinaryScalaNode("XOR", l, r, (l, r) => l ^ r)

sealed abstract class BinaryScalaNode(operator: String, left: ScalaNode, right: ScalaNode, eval: (Boolean, Boolean) => Boolean) extends ScalaNode {

  override def apply(assignment: BoolAssignment) = eval.apply(left(assignment), right(assignment))

  override def negate: ScalaNode = this match {
    case AndScalaNode(l, r) => l nand r

    case NAndScalaNode(l, r) => l and r

    case OrScalaNode(l, r) => l nor r

    case NOrScalaNode(l, r) => l or r

    case XOrScalaNode(l, r) => l equiv r

    case Equivalency(l, r) => l xor r

    case Implication(l, r) => l and r.negate
  }

  override def getAsString(needsParans: Boolean): String = {
    val inner = left.getAsString(left.isInstanceOf[BinaryScalaNode]) + " " + operator.toLowerCase + " " + right.getAsString(right.isInstanceOf[BinaryScalaNode])
    if (needsParans) "(" + inner + ")" else inner
  }

  override def usedVariables: Set[Variable] = left.usedVariables ++ right.usedVariables

}

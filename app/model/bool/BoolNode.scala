package model.bool

import model.bool.ScalaNode._

import scala.annotation.tailrec

sealed abstract class ScalaNode {

  def apply(assignment: BoolTableRow): Boolean

  def negate: ScalaNode

  def getAsString(needsParans: Boolean): String

  def usedVariables: Set[Variable]

  def and(that: ScalaNode): AndScalaNode = AndScalaNode(this, that)

  def or(that: ScalaNode): OrScalaNode = OrScalaNode(this, that)

  def nand(that: ScalaNode): NAndScalaNode = NAndScalaNode(this, that)

  def nor(that: ScalaNode): NOrScalaNode = NOrScalaNode(this, that)

  def xor(that: ScalaNode): XOrScalaNode = XOrScalaNode(this, that)

  def impl(that: ScalaNode): Implication = Implication(this, that)

  def equiv(that: ScalaNode): Equivalency = Equivalency(this, that)

  def unary_-(): NotScalaNode = NotScalaNode(this)

  override def toString: String = getAsString(false)

  def asString: String = getAsString(false)

  def asHtml: String = {

    @tailrec
    def go(formula: String, replacers: List[(String, String)]): String = replacers match {
      case Nil                  => formula
      case (key, value) :: tail => go(formula.replaceAll(key, value), tail)
    }

    go(getAsString(false), HtmlReplacers.toList)
  }

}

object ScalaNode {

  val HtmlReplacers: Map[String, String] = Map[String, String](
    "impl" -> "&rArr;",
    "nor" -> "&#x22bd;",
    "nand" -> "&#x22bc;",
    "equiv" -> "&hArr;",
    "not " -> "&not;",
    "and" -> "&and;",
    "xor" -> "&oplus;",
    "or" -> "&or;")

  def not(child: ScalaNode): NotScalaNode = NotScalaNode(child)

  def constant(value: Boolean): Constant = if (value) TRUE else FALSE

}

object FALSE extends Constant(false)

final case class NAndScalaNode(l: ScalaNode, r: ScalaNode) extends BinaryScalaNode("NAND", l, r, (l, r) => !(l && r))

final case class Implication(l: ScalaNode, r: ScalaNode) extends BinaryScalaNode("IMPL", l, r, (l, r) => !l || r)

final case class Equivalency(l: ScalaNode, r: ScalaNode) extends BinaryScalaNode("EQUIV", l, r, (l, r) => l == r)

final case class NOrScalaNode(l: ScalaNode, r: ScalaNode) extends BinaryScalaNode("NOR", l, r, (l, r) => !(l || r))

final case class NotScalaNode(child: ScalaNode) extends ScalaNode {

  override def apply(assignment: BoolTableRow): Boolean = !child(assignment)

  override def negate: ScalaNode = child

  override def getAsString(needsParans: Boolean): String = {
    val inner = "not " + child.getAsString(needsParans)
    if (needsParans) "(" + inner + ")" else inner
  }

  override def usedVariables: Set[Variable] = child.usedVariables

}

final case class OrScalaNode(l: ScalaNode, r: ScalaNode) extends BinaryScalaNode("OR", l, r, (l, r) => l || r)

sealed abstract class Constant(value: Boolean) extends ScalaNode {

  override def apply(assignment: BoolTableRow): Boolean = value

  override def negate: ScalaNode = if (value) FALSE else TRUE

  override def getAsString(needsParans: Boolean): String = if (value) "1" else "0"

  override def usedVariables: Set[Variable] = Set.empty
}

object TRUE extends Constant(true)

final case class Variable(variable: Char) extends ScalaNode with Ordered[Variable] {

  override def apply(assignment: BoolTableRow) = assignment(this)

  override def negate: ScalaNode = not(this)

  override def getAsString(needsParans: Boolean): String = variable.toString

  override def usedVariables = Set(this)

  override def compare(that: Variable): Int = variable - that.variable

}

final case class AndScalaNode(l: ScalaNode, r: ScalaNode) extends BinaryScalaNode("AND", l, r, (l, r) => l && r)

final case class XOrScalaNode(l: ScalaNode, r: ScalaNode) extends BinaryScalaNode("XOR", l, r, (l, r) => l ^ r)

sealed abstract class BinaryScalaNode(operator: String, left: ScalaNode, right: ScalaNode, eval: (Boolean, Boolean) => Boolean) extends ScalaNode {

  override def apply(assignment: BoolTableRow) = eval.apply(left(assignment), right(assignment))

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
    def isBinaryNode(node: ScalaNode): Boolean = node match {
      case _: BinaryScalaNode => true
      case _                  => false
    }

    val inner = left.getAsString(isBinaryNode(left)) + " " + operator.toLowerCase + " " + right.getAsString(isBinaryNode(right))
    if (needsParans) "(" + inner + ")" else inner
  }

  override def usedVariables: Set[Variable] = left.usedVariables ++ right.usedVariables

}

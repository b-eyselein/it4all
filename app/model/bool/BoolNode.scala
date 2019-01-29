package model.bool

import model.bool.BoolNode._

sealed abstract class BoolNode {

  def apply(assignment: BoolTableRow): Boolean

  def negate: BoolNode

  def getAsString(needsParans: Boolean): String

  def usedVariables: Set[Variable]

  def and(that: BoolNode): AndBoolNode = AndBoolNode(this, that)

  def or(that: BoolNode): OrBoolNode = OrBoolNode(this, that)

  def nand(that: BoolNode): NAndBoolNode = NAndBoolNode(this, that)

  def nor(that: BoolNode): NOrBoolNode = NOrBoolNode(this, that)

  def xor(that: BoolNode): XOrBoolNode = XOrBoolNode(this, that)

  def impl(that: BoolNode): Implication = Implication(this, that)

  def equiv(that: BoolNode): Equivalency = Equivalency(this, that)

  def unary_-(): NotBoolNode = NotBoolNode(this)

  override def toString: String = getAsString(false)

  def asString: String = getAsString(false)

  def asHtml: String = {

    @annotation.tailrec
    def go(formula: String, replacers: List[(String, String)]): String = replacers match {
      case Nil                  => formula
      case (key, value) :: tail => go(formula.replaceAll(key, value), tail)
    }

    go(getAsString(false), HtmlReplacers.toList)
  }

}

object BoolNode {

  val HtmlReplacers: Map[String, String] = Map[String, String](
    "impl" -> "&rArr;",
    "nor" -> "&#x22bd;",
    "nand" -> "&#x22bc;",
    "equiv" -> "&hArr;",
    "not " -> "&not;",
    "and" -> "&and;",
    "xor" -> "&oplus;",
    "or" -> "&or;")

  def not(child: BoolNode): NotBoolNode = NotBoolNode(child)

  def constant(value: Boolean): Constant = if (value) TRUE else FALSE

}

// Not

final case class NotBoolNode(child: BoolNode) extends BoolNode {

  override def apply(assignment: BoolTableRow): Boolean = !child(assignment)

  override def negate: BoolNode = child

  override def getAsString(needsParans: Boolean): String = {
    val inner = "not " + child.getAsString(needsParans)
    if (needsParans) "(" + inner + ")" else inner
  }

  override def usedVariables: Set[Variable] = child.usedVariables

}

// Constants

sealed abstract class Constant(value: Boolean) extends BoolNode {

  override def apply(assignment: BoolTableRow): Boolean = value

  override def negate: BoolNode = if (value) FALSE else TRUE

  override def getAsString(needsParans: Boolean): String = if (value) "1" else "0"

  override def usedVariables: Set[Variable] = Set.empty

}

object FALSE extends Constant(false)

object TRUE extends Constant(true)

// Variable nodes

final case class Variable(variable: Char) extends BoolNode with Ordered[Variable] {

  override def apply(assignment: BoolTableRow): Boolean = assignment(this)

  override def negate: BoolNode = not(this)

  override def getAsString(needsParans: Boolean): String = variable.toString

  override def usedVariables: Set[Variable] = Set(this)

  override def compare(that: Variable): Int = variable - that.variable

}

// Binary nodes

sealed abstract class BinaryBoolNode(operator: String, left: BoolNode, right: BoolNode, eval: (Boolean, Boolean) => Boolean) extends BoolNode {

  override def apply(assignment: BoolTableRow): Boolean = eval.apply(left(assignment), right(assignment))

  override def getAsString(needsParans: Boolean): String = {
    def isBinaryNode(node: BoolNode): Boolean = node match {
      case _: BinaryBoolNode => true
      case _                 => false
    }

    val inner = left.getAsString(isBinaryNode(left)) + " " + operator.toLowerCase + " " + right.getAsString(isBinaryNode(right))
    if (needsParans) "(" + inner + ")" else inner
  }

  override def usedVariables: Set[Variable] = left.usedVariables ++ right.usedVariables

}


final case class OrBoolNode(l: BoolNode, r: BoolNode) extends BinaryBoolNode("OR", l, r, (l, r) => l || r) {

  override def negate: BoolNode = l nor r

}

final case class AndBoolNode(l: BoolNode, r: BoolNode) extends BinaryBoolNode("AND", l, r, (l, r) => l && r) {

  override def negate: BoolNode = l nand r

}

final case class XOrBoolNode(l: BoolNode, r: BoolNode) extends BinaryBoolNode("XOR", l, r, (l, r) => l ^ r) {

  override def negate: BoolNode = l equiv r

}


final case class NAndBoolNode(l: BoolNode, r: BoolNode) extends BinaryBoolNode("NAND", l, r, (l, r) => !(l && r)) {

  override def negate: BoolNode = l and r

}

final case class Implication(l: BoolNode, r: BoolNode) extends BinaryBoolNode("IMPL", l, r, (l, r) => !l || r) {

  override def negate: BoolNode = l and r.negate

}

final case class Equivalency(l: BoolNode, r: BoolNode) extends BinaryBoolNode("EQUIV", l, r, (l, r) => l == r) {

  override def negate: BoolNode = l xor r

}

final case class NOrBoolNode(l: BoolNode, r: BoolNode) extends BinaryBoolNode("NOR", l, r, (l, r) => !(l || r)) {

  override def negate: BoolNode = l or r

}
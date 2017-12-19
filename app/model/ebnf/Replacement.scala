package model.ebnf

import model.ebnf.Replacement._

// abstract superclass

object Replacement {
  val seqOperator = ","
  val altOperator = "|"

  val optOperator  = "?"
  val repOperator  = "*"
  val rep1Operator = "+"
}

trait Replacement {

  def asString: String

  override def toString: String = asString

  def * = Repetition(this)

  def + = Min1Repetition(this)

  def ? = OptionReplacement(this)

  def ~(that: Replacement) = Sequence(this, that)

  def |(that: Replacement) = Alternative(this, that)

  def getReplacements(rules: Map[Variable, Replacement]): Seq[Seq[Replacement]]

}

// abstract kinds of replacements

abstract class BinaryReplacement(val left: Replacement, val right: Replacement, op: String) extends Replacement {

  override def asString = s"$left $op $right"

}

abstract class UnaryReplacement(val child: Replacement, val operator: String) extends Replacement {

  override def asString: String = child + operator

}

abstract class SymbolReplacement(val symbol: Symbol) extends Replacement {

  override def asString: String = symbol.toString

}

// Real kinds of replacements

case class Sequence(l: Replacement, r: Replacement) extends BinaryReplacement(l, r, seqOperator) {

  override def getReplacements(rules: Map[Variable, Replacement]): Seq[Seq[Replacement]] = Seq(Seq(l, r))

}

case class Alternative(l: Replacement, r: Replacement) extends BinaryReplacement(l, r, altOperator) {

  override def getReplacements(rules: Map[Variable, Replacement]): Seq[Seq[Replacement]] = Seq(Seq(l), Seq(r))

}


case class OptionReplacement(c: Replacement) extends UnaryReplacement(c, optOperator) {

  override def getReplacements(rules: Map[Variable, Replacement]): Seq[Seq[Replacement]] = Seq(Seq.empty, Seq(c))

}

case class Repetition(c: Replacement) extends UnaryReplacement(c, repOperator) {

  override def getReplacements(rules: Map[Variable, Replacement]): Seq[Seq[Replacement]] = Seq(Seq.empty, Seq(c), Seq(c, c), Seq(c, c, c))

}

case class Min1Repetition(c: Replacement) extends UnaryReplacement(c, rep1Operator) {

  override def getReplacements(rules: Map[Variable, Replacement]): Seq[Seq[Replacement]] = Seq(Seq(c), Seq(c, c), Seq(c, c, c))

}


case class Grouping(child: Replacement) extends Replacement {

  override def asString: String = "(" + child + ")"

  override def getReplacements(rules: Map[Variable, Replacement]): Seq[Seq[Replacement]] = Seq(Seq(child))

}


case class TerminalReplacement(t: Terminal) extends SymbolReplacement(t) {

  override def getReplacements(rules: Map[Variable, Replacement]): Seq[Seq[Replacement]] = Seq.empty

}

case class VariableReplacement(v: Variable) extends SymbolReplacement(v) {

  override def getReplacements(rules: Map[Variable, Replacement]): Seq[Seq[Replacement]] = Seq(rules get v map (Seq(_)) getOrElse Seq.empty)

}

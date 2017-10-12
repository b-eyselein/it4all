package model.ebnf

// abstract superclass

trait Replacement {
  def asString: String

  override def toString: String = asString

  def * = Repetition(this)

  def + = Min1Repetition(this)

  def ? = OptionReplacement(this)

  def ~(that: Replacement) = Sequence(this, that)

  def |(that: Replacement) = Alternative(this, that)
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

case class Sequence(l: Replacement, r: Replacement) extends BinaryReplacement(l, r, Replacement.seqOperator)

case class Alternative(l: Replacement, r: Replacement) extends BinaryReplacement(l, r, Replacement.altOperator)

case class OptionReplacement(c: Replacement) extends UnaryReplacement(c, Replacement.optOperator)

case class Repetition(c: Replacement) extends UnaryReplacement(c, Replacement.repOperator)

case class Min1Repetition(c: Replacement) extends UnaryReplacement(c, Replacement.rep1Operator)

case class Grouping(child: Replacement) extends Replacement {
  override def asString: String = "(" + child + ")"
}

case class TerminalReplacement(t: Terminal) extends SymbolReplacement(t)

case class VariableReplacement(v: Variable) extends SymbolReplacement(v)

object Replacement {
  val seqOperator = ","
  val altOperator = "|"

  val optOperator = "?"
  val repOperator = "*"
  val rep1Operator = "+"
}
package model.ebnf

abstract class Replacement {
  def asString: String
  override def toString = asString
}

abstract class UnaryReplacement(val child: Replacement, val operator: String) extends Replacement {
  override def asString = child + operator
}

case class Sequence(children: List[Replacement]) extends Replacement {
  override def asString = children.mkString(s" ${Replacement.seqOperator}  ")
}

case class Alternative(children: List[Replacement]) extends Replacement {
  override def asString = children.mkString(s" ${Replacement.altOperator} ")
}

case class Option(c: Replacement) extends UnaryReplacement(c, Replacement.optOperator)

case class Repetition(c: Replacement) extends UnaryReplacement(c, Replacement.repOperator)

case class Min1Repetition(c: Replacement) extends UnaryReplacement(c, Replacement.rep1Operator)

case class Grouping(child: Replacement) extends Replacement {
  override def asString = "(" + child + ")"
}

case class VarReplacement(variable: Variable) extends Replacement {
  override def asString = variable.name
}

case class TerminalReplacement(terminal: TerminalSymbol) extends Replacement {
  override def asString = terminal.name
}

object Replacement {
  val seqOperator = ","
  val altOperator = "|"

  val optOperator = "?"
  val repOperator = "*"
  val rep1Operator = "+"
}
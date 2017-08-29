package model.ebnf

abstract sealed class Replacement {
  def asString: String
}

case class Sequence(children: List[Replacement]) extends Replacement {
  override def asString = children.mkString(" ")
}

case class Alternative(children: List[Replacement]) extends Replacement {
  override def asString = children.mkString(" | ")
}

case class Option(child: Replacement) extends Replacement {
  override def asString = child + "?"
}

case class Repetition(child: Replacement) extends Replacement {
  override def asString = child + "*"
}

case class Min1Repetition(child: Replacement) extends Replacement {
  override def asString = child + "+"
}

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
  
  def parse(str: String) = new Sequence(str.split(" ").map(s => new VarReplacement(new Variable(s))).toList)
  
}
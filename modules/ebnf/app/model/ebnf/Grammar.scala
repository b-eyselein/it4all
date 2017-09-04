package model.ebnf

import scala.collection.JavaConverters._

class Symbol(val name: String)

case class TerminalSymbol(n: String) extends Symbol(n) {
  override def toString = s"'$n'"
}

case class Variable(n: String) extends Symbol(n) {
  override def toString = n
}

case class Rule(left: Variable, right: Replacement) {
  override def toString = s"$left = $right"
}

case class Grammar(terminals: List[TerminalSymbol], variables: List[Variable], startSymbol: Variable, rules: List[Rule]) 
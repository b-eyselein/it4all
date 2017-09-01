package model.ebnf

import scala.collection.JavaConverters._

class Symbol(val name: String)

case class TerminalSymbol(n: String) extends Symbol(n)

case class Variable(n: String) extends Symbol(n)

case class Rule(left: Variable, right: Replacement)

case class Grammar(terminals: List[TerminalSymbol], variables: List[Variable], startSymbol: Variable, rules: List[Rule]) {

  def this(ters: java.util.List[TerminalSymbol], vars: java.util.List[Variable], startSymbol: Variable, rs: java.util.List[Rule]) =
    this(ters.asScala.toList, vars.asScala.toList, startSymbol, rs.asScala.toList)

}
package model.ebnf

class Symbol(val name: String)

case class TerminalSymbol(n: String) extends Symbol(n)

case class Variable(n: String) extends Symbol(n)

case class Rule(left: Variable, right: Replacement)

case class Grammar(terminals: List[TerminalSymbol], variables: List[Variable], startSymbol: Variable, rules: List[Rule])
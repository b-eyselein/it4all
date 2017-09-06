package model.ebnf

abstract class Symbol

case class Variable(variable: String) extends Symbol {
  override def toString = variable
}

case class Terminal(name: String) extends Symbol {
  override def toString = s"'$name'"
}

case class Grammar(terminals: List[Terminal], variables: List[Variable], startSymbol: Variable, rules: Map[Variable, Replacement]) {

  val MaxDepth = 3

  def deriveAll(): List[String] = {
    val startReplacement = rules(startSymbol)
    
    println(startReplacement)
    
    List.empty
  }

}
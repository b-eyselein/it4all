package model.ebnf

import Grammar._

abstract class Symbol

case class Variable(variable: String) extends Symbol {
  override def toString: String = variable
}

case class Terminal(name: String) extends Symbol {
  override def toString = s"'$name'"
}

object Grammar {
  val MaxDepth = 3
}

case class Grammar(terminals: List[Terminal], variables: List[Variable], startSymbol: Variable, rules: Map[Variable, Replacement]) {

  def deriveAll(): List[String] = {
    val startReplacement = rules(startSymbol)

    println(startReplacement)

    List.empty
  }

}
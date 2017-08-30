package model.ebnf;

import org.junit.Test

class GrammarTest {

  implicit def string2TerminalSymbol(str: String) = new TerminalSymbol(str)
  implicit def string2Variable(str: String) = new Variable(str)

  val vars: List[Variable] = List("A", "B", "S")

  @Test
  def test() {
    val terminals: List[TerminalSymbol] = List("0", "1")

    val rules: List[Rule] = RuleParser.parseRules(List("S = A, B", "A = '0'", "B = '1'"))

    val grammar = new Grammar(terminals, vars, vars(2), rules)

    assert(grammar.startSymbol == vars(2))
  }

}

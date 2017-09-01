package model.ebnf;

import org.junit.Test

class GrammarTest {

  val vars: List[Variable] = List(Variable("A"), Variable("B"), Variable("S"))

  @Test
  def test() {
    val terminals: List[TerminalSymbol] = List(TerminalSymbol("0"), TerminalSymbol("1"))

    val rules: List[Rule] = RuleParser.parseRules(List("S = A, B", "A = '0'", "B = '1'"))

    val grammar = new Grammar(terminals, vars, vars(2), rules)

    assert(grammar.startSymbol == vars(2))
  }

}

package model.ebnf;

import org.junit.Test

class GrammarTest {

  implicit def string2TerminalSymbol(str: String) = new TerminalSymbol(str)
  implicit def string2Variable(str: String) = new Variable(str)

  implicit def string2Replacement(str: String) = Replacement.parse(str)

  implicit def tupleToRule(tuple: (String, String)): Rule = new Rule(tuple._1, tuple._2)

  @Test
  def test() {
    val terminals: List[TerminalSymbol] = List("'0'", "'1'")

    val variables: List[Variable] = List("A", "B", "S")

    val startSymbol = "S"

    val rules: List[Rule] = List(
      "S" -> "A B",
      "A" -> "0",
      "B" -> "1")

    val grammar = new Grammar(terminals, variables, startSymbol, rules)
  }

}

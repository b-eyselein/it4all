package model.ebnf

import org.junit.Test

class GrammarTest {

  val terms = List(Terminal("0"), Terminal("1"))

  val (s, a, b, c) = (Variable("S"), Variable("A"), Variable("B"), Variable("C"))

  val (aRepl, bRepl, cRepl) = (VariableReplacement(a), VariableReplacement(b), VariableReplacement(c))
  val (nul, one) = (TerminalReplacement(Terminal("0")), TerminalReplacement(Terminal("1")))

  val rules: Map[Variable, Replacement] = Map(
    s -> (aRepl | bRepl),

    a -> (one ~ nul))

  @Test
  def testDeriveAll {
    val gr = new Grammar(terms, List(s, a, b, c), s, rules)
    gr.deriveAll()
  }

}
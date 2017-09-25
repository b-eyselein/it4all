package model.ebnf

import org.junit.Test

class ReplacementTest {

  val (a, b) = (Variable("A"), Variable("B"))
  val aRepl = VariableReplacement(a)
  val bRepl = VariableReplacement(b)
  val one = Terminal("1")

  @Test
  def testSequence = testReplacement(aRepl ~ aRepl, "A , A", List(List(aRepl, aRepl)))

  @Test
  def testAlternative = testReplacement(aRepl | bRepl, "A | B", List(List(aRepl), List(bRepl)))

  @Test
  def testOption = testUnaryReplacement(aRepl?, aRepl, a.toString + "?", List(List.empty, List(aRepl)))

  @Test
  def testRepetition = testUnaryReplacement(aRepl*, aRepl, a.toString + "*",
    List(List.empty, List(aRepl), List(aRepl, aRepl), List(aRepl, aRepl, aRepl)))

  @Test
  def testMin1Repetition = testUnaryReplacement(aRepl+, aRepl, a.toString + "+",
    List(List(aRepl), List(aRepl, aRepl), List(aRepl, aRepl, aRepl)))

  @Test
  def testGrouping = testReplacement(Grouping(aRepl), "(A)", List.empty)

  @Test
  def testTerminal = testSymbolReplacement(TerminalReplacement(one), one, "'1'", List.empty)

  @Test
  def testVariable = testSymbolReplacement(aRepl, a, "A", List.empty)

  private def testUnaryReplacement(repl: UnaryReplacement, child: Replacement, expString: String, expReplacements: List[List[Replacement]]) {
    assert(repl.child == child)
    testReplacement(repl, expString, expReplacements)
  }

  private def testSymbolReplacement(repl: SymbolReplacement, symbol: Symbol, expStr: String, expRepl: List[List[Replacement]]) {
    assert(repl.symbol == symbol,
      s"Expected that symbol of $repl has a name of '$symbol', but got '${repl.symbol}'")
    testReplacement(repl, expStr, expRepl)
  }

  private def testReplacement(repl: Replacement, expString: String, expReplaments: List[List[Replacement]]) {
    assert(repl.asString == expString,
      s"Expected that a replacement of $repl has a string representation of '$expString', but got '${repl.asString}'")
//    assert(repl.getAllReplacements == expReplaments,
//      s"Expected that a replacement of $repl has combined repl of \n\t$expReplaments, but got \n\t${repl.getAllReplacements}")
  }

}
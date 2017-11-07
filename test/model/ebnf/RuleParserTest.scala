package model.ebnf

import org.junit.Test

class RuleParserTest {

  //  val (a, b, c, d, e, s) = (Variable("A"), Variable("B"), Variable("C"), Variable("D"), Variable("E"), Variable("S"))
  //  val (nul, one) = (TerminalSymbol("0"), TerminalSymbol("1"))

  def testRuleParse(ruleStr: String, ruleExpLeft: Variable, ruleExpRight: Replacement) {
    //    val ruleParsed = RuleParser.parse(ruleStr)
    //
    //    assert(ruleParsed.left == ruleExpLeft, s"Expected that left var of parsed rule $ruleStr is $ruleExpLeft but was ${ruleParsed.left}")
    //    assert(ruleParsed.right == ruleExpRight, s"Expected that replacement of parsed rule $ruleStr is\n$ruleExpRight\n\tbut was\n${ruleParsed.right}")
  }

  @Test
  def testParse() {
    //    testRuleParse("S = A", s, a)
    //
    //    testRuleParse("A = B, C", a, Sequence(b, c))
    //
    //    testRuleParse("B = '1' | C", b, one | c)
    //
    //    testRuleParse("C = '1'?, D, '0'+", c, ~one + d + +nul)
    //
    //    testRuleParse("D = '1'+ | '0'+ | ('1', '0')", d, +one | +nul | Grouping(one + nul))
    //
    //    testRuleParse("E = '0'*", e, Repetition(nul))
  }

  @Test
  def testParseReplacement() {
    //    val res = RuleParser.parseReplacement("'1' '0' '1' '0'")
  }

}
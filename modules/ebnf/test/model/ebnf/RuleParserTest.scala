package model.ebnf

import org.junit.Test

class RuleParserTest {

  val (a, b, c, d, s) = (Variable("A"), Variable("B"), Variable("C"), Variable("D"), Variable("S"))
  val (nul, one) = (TerminalReplacement(TerminalSymbol("0")), TerminalReplacement(TerminalSymbol("1")))

  def testRuleParse(ruleStr: String, ruleExpLeft: Variable, ruleExpRight: Replacement) {
    val ruleParsed = RuleParser.parse(ruleStr)

    assert(ruleParsed.left == ruleExpLeft, s"Expected that left var of parsed rule $ruleStr is $ruleExpLeft but was ${ruleParsed.left}")
    assert(ruleParsed.right == ruleExpRight, s"Expected that replacement of parsed rule $ruleStr is\n$ruleExpRight\n\tbut was\n${ruleParsed.right}")
  }

  @Test
  def testParse = {

    testRuleParse("S = A", s, VarReplacement(a))

    testRuleParse("A = B, C", a, Sequence(List(VarReplacement(b), VarReplacement(c))))

    testRuleParse("B = '1' | C", b, Alternative(List(one, VarReplacement(c))))

    testRuleParse("C = '1'?, D, '0'+", c, Sequence(List(Option(one), VarReplacement(d), Min1Repetition(nul))))

    testRuleParse("D = '0'*", d, Repetition(nul))

  }

}
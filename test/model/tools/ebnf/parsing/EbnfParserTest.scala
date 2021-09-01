package model.tools.ebnf.parsing

import model.tools.ebnf.TestValues
import model.tools.ebnf.grammar._
import model.tools.ebnf.parsing.ExtendedEbnfParser._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EbnfParserTest extends AnyFlatSpec with Matchers with TestValues {

  behavior of "EbnfParser"

  it should "parse NonTerminalSymbol" in {
    parse(variable, "A").get shouldBe A
    parse(variable, "Z").get shouldBe Variable("Z")
    parse(variable, "C1").get shouldBe Variable("C", Some(1))
    //    parse(nonTerminalSymbol, "ACA") map { nts => nts shouldBe ExtendedEbnfNonTerminalSymbol("ACA") }

    parse(variable, "'1'").successful shouldBe false
  }

  it should "parse TerminalSymbol" in {
    parse(terminal, "'1'").get shouldBe termOne
    parse(terminal, "'a'").get shouldBe Terminal("a")
  }

  // collection elements

  it should "parse Alternatives" in {
    parse(alternative, "A | B").get shouldBe A | B
    parse(alternative, "'1' | B").get shouldBe termOne | B
    parse(alternative, "A | '1'").get shouldBe A | termOne

    parse(alternative, "A | B | C").get shouldBe A | B | C
    parse(alternative, "A | '1' | C").get shouldBe A | termOne | C

    parse(alternative, "A | B | C | D").get shouldBe A | B | C | D
    parse(alternative, "A | '1' | C | '0'").get shouldBe A | termOne | C | termNull
  }

  it should "parse Sequences" in {
    parse(sequence, "A B").get shouldBe A ~ B

    parse(sequence, "'1' B").get shouldBe termOne ~ B
    parse(sequence, "A '1'").get shouldBe A ~ termOne

    parse(sequence, "A B C").get shouldBe A ~ B ~ C
    parse(sequence, "A '1' C").get shouldBe A ~ termOne ~ C

    parse(sequence, "A B C D").get shouldBe A ~ B ~ C ~ D
    parse(sequence, "A '1' C '0'").get shouldBe A ~ termOne ~ C ~ termNull

    parse(sequence, "A B | C").get shouldBe (A ~ (B | C))
  }

  // unary elements

  it should "parse Optionals" in {
    parse(optional, "'1'?").get shouldBe termOne.?
    parse(optional, "A?").get shouldBe A.?
  }

  it should "parse RepAny" in {
    parse(repAny, "'1'*").get shouldBe termOne.*
    parse(repAny, "'1'*").get shouldBe termOne.*
  }

  it should "parse RepOne" in {
    parse(repOne, "'1'+").get shouldBe termOne.+
    parse(repOne, "A+").get shouldBe A.+
  }

  it should "parse grammars" in {

  }

}

package model.conversion

import model.DefaultGrammars.{binaryPalindromesBnf, binaryPalindromesGrammarEbnf}
import model.TestValues
import model.grammar._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

// https://stackoverflow.com/questions/2466484/converting-ebnf-to-bnf

class EbnfToBnfConverterTest extends AnyFlatSpec with Matchers with TestValues {

  behavior of "EbnfToBnfConverter"

  it should "find new terminal symbols" in {
    EbnfToBnfConverter.findNewVariable(Seq.empty) shouldBe A

    val existingNonTerminals1 = ('A' to 'E').map(cl => Variable(cl.toString))
    EbnfToBnfConverter.findNewVariable(existingNonTerminals1) shouldBe Variable("F")

    // All capital letters used, add numbers
    val existingNonTerminals2 = ('A' to 'Z').map(cl => Variable(cl.toString))
    EbnfToBnfConverter.findNewVariable(existingNonTerminals2) shouldBe Variable("A", Some(0))
  }

  it should "convert Ebnf grammars" in {
    val ebnfGrammar0 = ExtendedBackusNaurFormGrammar(A, Map.empty)
    val bnfGrammar0  = BackusNaurFormGrammar(A, Map.empty)
    EbnfToBnfConverter.convert(ebnfGrammar0) shouldBe bnfGrammar0

    val ebnfGrammar1 = ExtendedBackusNaurFormGrammar(
      A,
      Map(
        A -> (B ~ x),
        B -> y
      )
    )
    val bnfGrammar1 = BackusNaurFormGrammar(
      A,
      Map(
        A -> (B and x),
        B -> y
      )
    )
    EbnfToBnfConverter.convert(ebnfGrammar1) shouldBe bnfGrammar1

    val ebnfGrammar2 = ExtendedBackusNaurFormGrammar(
      startSymbol = A,
      Map(
        A -> (B.* | (C.* ~ A) | (A ~ B.*)),
        B -> (y ~ z),
        C -> (z | x)
      )
    )
    val bnfGrammar2 = BackusNaurFormGrammar(
      startSymbol = A,
      Map(
        A -> (D or (E and A) or (A and D)),
        B -> (y and z),
        C -> (z or x),
        D -> (EmptyWord or (B and D)),
        E -> (EmptyWord or (C and E))
      )
    )
    EbnfToBnfConverter.convert(ebnfGrammar2) shouldBe bnfGrammar2

    // binary palindromes
    EbnfToBnfConverter.convert(binaryPalindromesGrammarEbnf) shouldBe binaryPalindromesBnf
  }

}

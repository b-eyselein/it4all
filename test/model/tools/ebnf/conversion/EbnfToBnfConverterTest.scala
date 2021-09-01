package model.tools.ebnf.conversion

import model.tools.ebnf.DefaultGrammars.{binaryPalindromesBnf, binaryPalindromesGrammarEbnf}
import model.tools.ebnf.TestValues
import model.tools.ebnf.grammar._
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
    val emptyEbnfGrammar = ExtendedBackusNaurFormGrammar(A, Map.empty)
    val emptyBnfGrammar  = BackusNaurFormGrammar(A, Map.empty)
    EbnfToBnfConverter.convert(emptyEbnfGrammar) shouldBe emptyBnfGrammar

    val ebnfGrammar0 = ExtendedBackusNaurFormGrammar(
      startSymbol = A,
      Map(
        A -> B.?,
        B -> C.*,
        C -> s.+
      )
    )
    val bnfGrammar0 = BackusNaurFormGrammar(
      startSymbol = A,
      Map(
        A -> (EmptyWord or B),
        B -> (EmptyWord or (C and B)),
        C -> (s or (s and C))
      )
    )
    EbnfToBnfConverter.convert(ebnfGrammar0) shouldBe bnfGrammar0

    val ebnfGrammar1 = ExtendedBackusNaurFormGrammar(
      A,
      Map(
        A -> (B ~ s),
        B -> t
      )
    )
    val bnfGrammar1 = BackusNaurFormGrammar(
      A,
      Map(
        A -> (B and s),
        B -> t
      )
    )
    EbnfToBnfConverter.convert(ebnfGrammar1) shouldBe bnfGrammar1

    val ebnfGrammar2 = ExtendedBackusNaurFormGrammar(
      startSymbol = A,
      Map(
        A -> (B.* | (C.* ~ A) | (A ~ B.*)),
        B -> (t ~ u),
        C -> (u | s)
      )
    )
    val bnfGrammar2 = BackusNaurFormGrammar(
      startSymbol = A,
      Map(
        A -> (D or (E and A) or (A and D)),
        B -> (t and u),
        C -> (u or s),
        D -> (EmptyWord or (B and D)),
        E -> (EmptyWord or (C and E))
      )
    )
    EbnfToBnfConverter.convert(ebnfGrammar2) shouldBe bnfGrammar2

    // binary palindromes
    EbnfToBnfConverter.convert(binaryPalindromesGrammarEbnf) shouldBe binaryPalindromesBnf
  }

}

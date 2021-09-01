package model.tools.ebnf.conversion

import model.tools.ebnf.TestValues
import model.tools.ebnf.grammar._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EbnfElToBnfElConverterTest extends AnyFlatSpec with Matchers with TestValues {

  it should "convert Ebnf collection elements" in {

    val result1 = EbnfToBnfConverter.convertCollectionChildElements(Seq(s, t, u))
    val awaited1 = CollectionGrammarElementConvResult[ExtendedBackusNaurFormElement, BackusNaurFormElement](
      newOutputElements = Seq(s, t, u)
    )
    result1 shouldBe awaited1

    val result2 = EbnfToBnfConverter.convertCollectionChildElements(
      children = Seq(s, B),
      currentVariables = Seq(B)
    )
    val awaited2 = CollectionGrammarElementConvResult[ExtendedBackusNaurFormElement, BackusNaurFormElement](
      newOutputElements = Seq(s, B),
      newVariables = Seq(B)
    )
    result2 shouldBe awaited2

    val result3 = EbnfToBnfConverter.convertCollectionChildElements(
      children = Seq(A.*, B),
      currentVariables = Seq(A, B)
    )
    val awaited3 = CollectionGrammarElementConvResult[ExtendedBackusNaurFormElement, BackusNaurFormElement](
      newOutputElements = Seq(C, B),
      newRules = Seq(C -> (EmptyWord | (A ~ C))),
      newVariables = Seq(A, B, C),
      newReplacers = Map(A.* -> C)
    )
    result3 shouldBe awaited3

    val result4 = EbnfToBnfConverter.convertCollectionChildElements(
      children = Seq(A, A ~ B.*),
      currentVariables = Seq(A, B)
    )
    val awaited4 = CollectionGrammarElementConvResult[ExtendedBackusNaurFormElement, BackusNaurFormElement](
      newOutputElements = Seq(A, A and C),
      newRules = Seq(C -> (EmptyWord | (B ~ C))),
      newVariables = Seq(A, B, C),
      newReplacers = Map(B.* -> C)
    )
    result4 shouldBe awaited4
  }

  it should "convert Ebnf Elements" in {
    // EmptyWord, NonTerminals and Terminals remain
    EbnfToBnfConverter.convertElement(EmptyWord) shouldBe GrammarElemConvResult(EmptyWord)

    EbnfToBnfConverter.convertElement(B) shouldBe GrammarElemConvResult(B)

    EbnfToBnfConverter.convertElement(s) shouldBe GrammarElemConvResult(s)

    // replace A? with B and new rule B -> eps | A
    val resultOptional = EbnfToBnfConverter.convertElement(EbnfOptional(A), Seq(A))
    val awaitedOptional = GrammarElemConvResult[ExtendedBackusNaurFormElement, BackusNaurFormElement](
      B,
      newRules = Seq(B -> (EmptyWord | A)),
      newVariables = Seq(A, B),
      newReplacers = Map(A.? -> B)
    )
    resultOptional shouldBe awaitedOptional

    // replace A* with B and B -> eps | (A B)
    val resultStar = EbnfToBnfConverter.convertElement(A.*, currentVariables = Seq(A))
    val awaitedStar = GrammarElemConvResult[ExtendedBackusNaurFormElement, BackusNaurFormElement](
      B,
      newRules = Seq(B -> (EmptyWord | (A ~ B))),
      newVariables = Seq(A, B),
      newReplacers = Map(A.* -> B)
    )
    resultStar shouldBe awaitedStar

    // replace A+ with B and new rule B -> A A*
    val resultPlus = EbnfToBnfConverter.convertElement(A.+, Seq(A))
    val awaitedPlus = GrammarElemConvResult[ExtendedBackusNaurFormElement, BackusNaurFormElement](
      B,
      newRules = Seq(B -> (A ~ A.*)),
      newVariables = Seq(A, B),
      newReplacers = Map(A.+ -> B)
    )
    resultPlus shouldBe awaitedPlus

    // replace A A B* with A A C and C -> eps | (B C)
    val result1 = EbnfToBnfConverter.convertElement(
      A ~ A ~ B.*,
      currentVariables = Seq(A, B)
    )
    val awaited1 = GrammarElemConvResult[ExtendedBackusNaurFormElement, BackusNaurFormElement](
      A and A and C,
      newRules = Seq(C -> (EmptyWord | (B ~ C))),
      newVariables = Seq(A, B, C),
      newReplacers = Map(B.* -> C)
    )
    result1 shouldBe awaited1

    // Replace A (A | B*) with A (A | C) and new rule C -> eps | (B C)
    val result2 = EbnfToBnfConverter.convertElement(
      A ~ (A | B.*),
      currentVariables = Seq(A, B)
    )
    val awaited2 = GrammarElemConvResult[ExtendedBackusNaurFormElement, BackusNaurFormElement](
      A and (A or C),
      newRules = Seq(C -> (EmptyWord | (B ~ C))),
      newVariables = Seq(A, B, C),
      newReplacers = Map(B.* -> C)
    )
    result2 shouldBe awaited2

    // Replace B* A B* with C A C and new rule C -> eps | (B C)
    val result3 = EbnfToBnfConverter.convertElement(
      ebnfEl = B.* ~ s ~ B.*,
      currentVariables = Seq(A, B)
    )
    val awaited3 = GrammarElemConvResult[ExtendedBackusNaurFormElement, BackusNaurFormElement](
      C and s and C,
      newRules = Seq(C -> (EmptyWord | (B ~ C))),
      newVariables = Seq(A, B, C),
      newReplacers = Map(B.* -> C)
    )
    result3 shouldBe awaited3
  }

}

package model.tools.ebnf.conversion

import model.tools.ebnf.TestValues
import model.tools.ebnf.grammar.{BackusNaurFormGrammar, BaseNormalizedFormGrammar, BaseNormalizedFormSequence, EmptyWord}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BaseNormalizationConverterTest extends AnyFlatSpec with Matchers with TestValues {

  import BaseNormalizationConverter._

  behavior of "BaseNormalizationConverter"

  private val randomGrammar = BackusNaurFormGrammar(
    sSlash,
    Map(
      sSlash -> S,
      S      -> C,
      C      -> D,
      D      -> (S or (s and S and t) or EmptyWord)
    )
  )

  private val randomGrammarNormalized = BaseNormalizedFormGrammar(
    sSlash,
    Map(
      sSlash -> S,
      S      -> C,
      C      -> D,
      D      -> alternatives(S, s + S + t, BaseNormalizedFormSequence(Seq(EmptyWord)))
    )
  )

  private val complexerGrammar1 = BackusNaurFormGrammar(
    sSlash,
    Map(
      sSlash -> S,
      S      -> ((A or B) and (C or D)),
      A      -> (termNull and termOne and termNull),
      B      -> termNull,
      C      -> termNull,
      D      -> termOne
    )
  )

  private val complexerGrammar1Normalized = BaseNormalizedFormGrammar(
    sSlash,
    Map(
      sSlash -> S,
      S      -> (E + F),
      E      -> alternatives(A, B),
      F      -> alternatives(C, D),
      A      -> (termNull + termOne + termNull),
      B      -> termNull,
      C      -> termNull,
      D      -> termOne
    )
  )

  it should "base normalize grammars" in {
    convert(randomGrammar) shouldBe randomGrammarNormalized

    convert(complexerGrammar1) shouldBe complexerGrammar1Normalized
  }

}

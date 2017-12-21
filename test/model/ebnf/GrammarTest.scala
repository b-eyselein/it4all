package model.ebnf

import org.junit.Test
import org.scalatest.Matchers._

import scala.language.implicitConversions

class GrammarTest {

  implicit def terminal2TerminalReplacement(symbol: Symbol): SymbolReplacement = symbol match {
    case v: Variable => VariableReplacement(v)
    case t: Terminal => TerminalReplacement(t)
  }

  val (s, a, b, c) = (Variable("S"), Variable("A"), Variable("B"), Variable("C"))
  val (nul, one)   = (Terminal("0"), Terminal("1"))


  @Test
  def testDeriveAll() {
    val gr1 = new Grammar(Seq(nul, one), List(s, a, b), s, RulesList(Map(
      s -> (a | b),
      a -> (one ~ nul)
    )))

    val derived1 = gr1.deriveAll
    derived1.size shouldBe 1
    derived1.head shouldBe "10"


    val gr2 = new Grammar(Seq(nul, one), List(s, a, b), s, RulesList(Map(
      s -> (a ~ b ~ a ~ b),
      a -> one,
      b -> nul
    )))

    val derived2 = gr2.deriveAll
    derived2.size shouldBe 1
    derived2.head shouldBe "1010"
  }

  @Test
  def deriveAllEvenBinaries(): Unit = {
    val gr3 = new Grammar(Seq(nul, one), List(s, a), s, RulesList(Map(
      s -> a ~ nul,
      a -> a.? ~ one
    )))

    val derived3 = gr3.deriveAll
    derived3.size shouldBe 10
    derived3.head shouldBe "10"
  }

}
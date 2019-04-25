package model.tools.bool

import model.tools.bool.BoolConsts.SolVariable
import org.scalatest.{FlatSpec, Matchers}

class BoolTableRowSpec extends FlatSpec with Matchers {

  behavior of "BoolTableRow"

  val av: Variable = Variable('a')
  val bv: Variable = Variable('b')
  val zv: Variable = SolVariable

  private val allFalse: Seq[BoolTableRow] = BoolTableRow.generateAllAssignments(Seq(av, bv)).map {
    _ + (zv -> false)
  }

  private val a_and_b: Seq[BoolTableRow] = BoolTableRow.generateAllAssignments(Seq(av, bv)).map {
    btr => btr + (zv -> (btr(av) && btr(bv)))
  }

  private val a_or_b: Seq[BoolTableRow] = BoolTableRow.generateAllAssignments(Seq(av, bv)).map {
    btr => btr + (zv -> (btr(av) || btr(bv)))
  }

  it should "generate normal forms" in {
    BoolTableRow.disjunktiveNormalForm(allFalse) shouldBe FALSE
    BoolTableRow.konjunktiveNormalForm(allFalse) shouldBe (-av or -bv).and(-av or bv).and(av or -bv).and(av or bv)

    BoolTableRow.disjunktiveNormalForm(a_and_b) shouldBe (av and bv)
    BoolTableRow.konjunktiveNormalForm(a_and_b) shouldBe (-av or -bv).and(-av or bv).and(av or -bv)

    BoolTableRow.disjunktiveNormalForm(a_or_b) shouldBe (-av and bv).or(av and -bv).or(av and bv)
    BoolTableRow.konjunktiveNormalForm(a_or_b) shouldBe (-av or -bv)
  }

}

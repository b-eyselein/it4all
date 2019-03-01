package model.tools.bool

import org.scalatest.{FlatSpec, Matchers}

import BoolConsts.SolVariable

class BoolTableRowSpec extends FlatSpec with Matchers {

  val av: Variable = Variable('a')
  val bv: Variable = Variable('b')
  val zv: Variable = SolVariable

  val allFalse: Seq[BoolTableRow] = BoolTableRow.generateAllAssignments(Seq(av, bv)).map {
    _ + (zv -> false)
  }

  val a_and_b: Seq[BoolTableRow] = BoolTableRow.generateAllAssignments(Seq(av, bv)).map {
    btr => btr + (zv -> (btr(av) && btr(bv)))
  }

  val a_or_b: Seq[BoolTableRow] = BoolTableRow.generateAllAssignments(Seq(av, bv)).map {
    btr => btr + (zv -> (btr(av) || btr(bv)))
  }

  "The object BoolTableRow" should "generate normal forms" in {
    assert(BoolTableRow.disjunktiveNormalForm(allFalse) == FALSE)
    assert(BoolTableRow.konjunktiveNormalForm(allFalse) == (-av or -bv).and(av or -bv).and(-av or bv).and(av or bv))

    assert(BoolTableRow.disjunktiveNormalForm(a_and_b) == (av and bv))
    assert(BoolTableRow.konjunktiveNormalForm(a_and_b) == (-av or -bv).and(av or -bv).and(-av or bv))

    assert(BoolTableRow.disjunktiveNormalForm(a_or_b) == (av and -bv).or(-av and bv).or(av and bv))
    assert(BoolTableRow.konjunktiveNormalForm(a_or_b) == (-av or -bv))
  }

}

package model.bool

import org.junit.Test

import scala.language.implicitConversions

class CreationQuestionTest  {

  implicit def char2Variable(char: Char): Variable = Variable(char)

  val (a, b, c, z): (Variable, Variable, Variable, Variable) = ('a', 'b', 'c', 'z')

  val awaitedDNF: ScalaNode = (-a and b and -c) or (-a and b and c) or (a and -b and c) or (a and b and c)
  val awaitedKNF: ScalaNode = (a or b or c) and (a or b or -c) and (-a or b or c) and (-a or -b or c)

  val assignments: List[BoolTableRow] = List(
    BoolTableRow(a -> false, b -> false, c -> false, z -> false),
    BoolTableRow(a -> false, b -> false, c -> true, z -> false),

    BoolTableRow(a -> false, b -> true, c -> false, z -> true),
    BoolTableRow(a -> false, b -> true, c -> true, z -> true),

    BoolTableRow(a -> true, b -> false, c -> false, z -> false),
    BoolTableRow(a -> true, b -> false, c -> true, z -> true),

    BoolTableRow(a -> true, b -> true, c -> false, z -> false),
    BoolTableRow(a -> true, b -> true, c -> true, z -> true))

  @Test
  def testDnf() {
    val dnf = BoolTableRow.disjunktiveNormalForm(CreationQuestion(assignments).solutions)
    assert(dnf == awaitedDNF,
      s"""Expected that DNF
         |  generated = $dnf
         |equals
         |  awaited = $awaitedDNF""".stripMargin)
  }

  @Test
  def testKnf() {
    val knf = BoolTableRow.konjunktiveNormalForm(CreationQuestion(assignments).solutions)
    assert(knf == awaitedKNF,
      s"""Expected that KNF
         |  generated = $knf
         |equals
         |  awaited = $awaitedKNF""".stripMargin)
  }

}
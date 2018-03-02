package model.bool

import model.bool.{BoolAssignment, CreationQuestion, ScalaNode, Variable}
import org.junit.Test

import scala.language.implicitConversions

class CreationQuestionTest  {

  implicit def char2Variable(char: Char): Variable = Variable(char)

  val (a, b, c, z): (Variable, Variable, Variable, Variable) = ('a', 'b', 'c', 'z')

  val awaitedDNF: ScalaNode = (-a and b and -c) or (-a and b and c) or (a and -b and c) or (a and b and c)
  val awaitedKNF: ScalaNode = (a or b or c) and (a or b or -c) and (-a or b or c) and (-a or -b or c)

  val assignments: List[BoolAssignment] = List(
    BoolAssignment(a -> false, b -> false, c -> false, z -> false),
    BoolAssignment(a -> false, b -> false, c -> true, z -> false),

    BoolAssignment(a -> false, b -> true, c -> false, z -> true),
    BoolAssignment(a -> false, b -> true, c -> true, z -> true),

    BoolAssignment(a -> true, b -> false, c -> false, z -> false),
    BoolAssignment(a -> true, b -> false, c -> true, z -> true),

    BoolAssignment(a -> true, b -> true, c -> false, z -> false),
    BoolAssignment(a -> true, b -> true, c -> true, z -> true))

  @Test
  def testDnf() {
    val dnf = BoolAssignment.disjunktiveNormalForm(CreationQuestion(assignments).solutions)
    assert(dnf == awaitedDNF,
      s"""Expected that DNF
         |  generated = $dnf
         |equals
         |  awaited = $awaitedDNF""".stripMargin)
  }

  @Test
  def testKnf() {
    val knf = BoolAssignment.konjunktiveNormalForm(CreationQuestion(assignments).solutions)
    assert(knf == awaitedKNF,
      s"""Expected that KNF
         |  generated = $knf
         |equals
         |  awaited = $awaitedKNF""".stripMargin)
  }

}
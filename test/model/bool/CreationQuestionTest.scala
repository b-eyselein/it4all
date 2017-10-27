package model.bool

import model.ScalaNode._
import model.{Assignment, CreationQuestion, ScalaNode, Variable}
import org.junit.Test

class CreationQuestionTest {

  val (a, b, c, z): (Variable, Variable, Variable, Variable) = ('a', 'b', 'c', 'z')

  val awaitedDNF: ScalaNode = (-a and b and -c) or (-a and b and c) or (a and -b and c) or (a and b and c)
  val awaitedKNF: ScalaNode = (a or b or c) and (a or b or -c) and (-a or b or c) and (-a or -b or c)

  val assignments: List[Assignment] = List(
    Assignment(a -> 0, b -> 0, c -> 0, z -> 0),
    Assignment(a -> 0, b -> 0, c -> 1, z -> 0),

    Assignment(a -> 0, b -> 1, c -> 0, z -> 1),
    Assignment(a -> 0, b -> 1, c -> 1, z -> 1),

    Assignment(a -> 1, b -> 0, c -> 0, z -> 0),
    Assignment(a -> 1, b -> 0, c -> 1, z -> 1),

    Assignment(a -> 1, b -> 1, c -> 0, z -> 0),
    Assignment(a -> 1, b -> 1, c -> 1, z -> 1))

  @Test
  def testDnf() = {

    val question = new CreationQuestion(List(a, b, c), assignments)

    val dnf = Assignment.getDisjunktiveNormalForm(question.solutions)
    assert(dnf == awaitedDNF, s"Expected that DNF\n\tgenerated = $dnf\nequals\n\tawaited = $awaitedDNF")

  }

  @Test
  def testKnf() = {
    val question = new CreationQuestion(List(a, b, c), assignments)

    val knf = Assignment.getKonjunktiveNormalForm(question.solutions)
    assert(knf == awaitedKNF, s"Expected that KNF\n\tgenerated = $knf\nequals\n\tawaited = $awaitedKNF")
  }

}
package model.bool

import org.scalatest._

import scala.language.implicitConversions

class NormalFormSpec extends FlatSpec with Matchers {

  implicit def char2Variable(char: Char): Variable = Variable(char)

  val (aVar, bVar, cVar, zVar): (Variable, Variable, Variable, Variable) = ('a', 'b', 'c', 'z')

  val awaitedDNF: ScalaNode = (-aVar and bVar and -cVar) or (-aVar and bVar and cVar) or (aVar and -bVar and cVar) or (aVar and bVar and cVar)
  val awaitedKNF: ScalaNode = (aVar or bVar or cVar) and (aVar or bVar or -cVar) and (-aVar or bVar or cVar) and (-aVar or -bVar or cVar)

  val assignments: List[BoolTableRow] = List(
    BoolTableRow(aVar -> false, bVar -> false, cVar -> false, zVar -> false),
    BoolTableRow(aVar -> false, bVar -> false, cVar -> true, zVar -> false),

    BoolTableRow(aVar -> false, bVar -> true, cVar -> false, zVar -> true),
    BoolTableRow(aVar -> false, bVar -> true, cVar -> true, zVar -> true),

    BoolTableRow(aVar -> true, bVar -> false, cVar -> false, zVar -> false),
    BoolTableRow(aVar -> true, bVar -> false, cVar -> true, zVar -> true),

    BoolTableRow(aVar -> true, bVar -> true, cVar -> false, zVar -> false),
    BoolTableRow(aVar -> true, bVar -> true, cVar -> true, zVar -> true)
  )

  "Both normal forms" should "be correctly generated" in {
    val completeAssignments = CreationQuestion(assignments).solutions

    awaitedDNF shouldBe BoolTableRow.disjunktiveNormalForm(completeAssignments)
    awaitedKNF shouldBe BoolTableRow.konjunktiveNormalForm(CreationQuestion(assignments).solutions)
  }

}
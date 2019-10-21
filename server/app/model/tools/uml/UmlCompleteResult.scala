package model.tools.uml

import model.core.matching.MatchingResult
import model.core.result.{CompleteResult, EvaluationResult, SuccessType}
import model.points._
import model.tools.uml.matcher._

object UmlCompleteResult {

  def describeImplementation(impl: UmlImplementation): String = s"${impl.subClass}  &rarr;  ${impl.superClass}"

  def describeAssociation(assoc: UmlAssociation): String =
    s"${assoc.assocType.german}: ${assoc.firstEnd} &harr; ${assoc.secondEnd} (${assoc.displayMult(turn = false)})"

}

final case class UmlCompleteResult(
  classResult: Option[MatchingResult[UmlClassMatch]],
  assocResult: Option[MatchingResult[UmlAssociationMatch]],
  implResult: Option[MatchingResult[UmlImplementationMatch]],
  points: Points, maxPoints: Points,
  solutionSaved: Boolean = false
) extends CompleteResult[EvaluationResult] {

  override val success: SuccessType = SuccessType.NONE

  override def results: Seq[EvaluationResult] = Seq[EvaluationResult]() ++ classResult ++ assocResult ++ implResult

}

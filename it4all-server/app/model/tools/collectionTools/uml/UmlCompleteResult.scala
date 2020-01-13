package model.tools.collectionTools.uml

import model.core.matching.{GenericAnalysisResult, MatchingResult}
import model.core.result.{CompleteResult, EvaluationResult, SuccessType}
import model.points._
import model.tools.collectionTools.uml.matcher._

object UmlCompleteResult {

  def describeImplementation(impl: UmlImplementation): String = s"${impl.subClass}  &rarr;  ${impl.superClass}"

  def describeAssociation(assoc: UmlAssociation): String =
    s"${assoc.assocType.german}: ${assoc.firstEnd} &harr; ${assoc.secondEnd} (${assoc.displayMult(turn = false)})"

}

final case class UmlCompleteResult(
  classResult: Option[MatchingResult[UmlClass, UmlClassMatchAnalysisResult, UmlClassMatch]],
  assocResult: Option[MatchingResult[UmlAssociation, UmlAssociationAnalysisResult, UmlAssociationMatch]],
  implResult: Option[MatchingResult[UmlImplementation, GenericAnalysisResult, UmlImplementationMatch]],
  points: Points,
  maxPoints: Points,
  solutionSaved: Boolean
) extends CompleteResult[EvaluationResult] {

  override val success: SuccessType = SuccessType.NONE

  override def results: Seq[EvaluationResult] = Seq[EvaluationResult]() ++ classResult ++ assocResult ++ implResult

}

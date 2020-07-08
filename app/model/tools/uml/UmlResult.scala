package model.tools.uml

import model.matching.MatchType
import model.points._
import model.result.{AbstractCorrectionResult, InternalErrorResult}
import model.tools.uml.UmlTool.{AssociationComparison, ClassComparison, ImplementationComparison}

trait UmlAbstractResult extends AbstractCorrectionResult[UmlAbstractResult]

final case class UmlInternalErrorResult(
  msg: String,
  maxPoints: Points,
  solutionSaved: Boolean = false
) extends UmlAbstractResult
    with InternalErrorResult[UmlAbstractResult] {

  override def updateSolutionSaved(solutionSaved: Boolean): UmlAbstractResult =
    this.copy(solutionSaved = solutionSaved)

}

final case class UmlResult(
  classResult: Option[ClassComparison],
  assocResult: Option[AssociationComparison],
  implResult: Option[ImplementationComparison],
  points: Points,
  maxPoints: Points,
  solutionSaved: Boolean = false
) extends UmlAbstractResult {

  override def isCompletelyCorrect: Boolean = {

    val classResultOk = classResult.forall(_.allMatches.forall(_.matchType == MatchType.SUCCESSFUL_MATCH))

    val assocResultOk = assocResult.forall(_.allMatches.forall(_.matchType == MatchType.SUCCESSFUL_MATCH))

    val implResultOk = implResult.forall(_.allMatches.forall(_.matchType == MatchType.SUCCESSFUL_MATCH))

    classResultOk && assocResultOk && implResultOk

  }

  override def updateSolutionSaved(solutionSaved: Boolean): UmlAbstractResult = this.copy(solutionSaved = solutionSaved)

}

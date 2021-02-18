package model.tools.uml

import model.matching.MatchType
import model.points._
import model.result.AbstractCorrectionResult
import model.tools.uml.UmlTool.{AssociationComparison, ClassComparison, ImplementationComparison}

final case class UmlResult(
  classResult: Option[ClassComparison],
  assocResult: Option[AssociationComparison],
  implResult: Option[ImplementationComparison],
  points: Points,
  maxPoints: Points
) extends AbstractCorrectionResult {

  override def isCompletelyCorrect: Boolean = {

    val classResultOk = classResult.forall(_.allMatches.forall(_.matchType == MatchType.SUCCESSFUL_MATCH))

    val assocResultOk = assocResult.forall(_.allMatches.forall(_.matchType == MatchType.SUCCESSFUL_MATCH))

    val implResultOk = implResult.forall(_.allMatches.forall(_.matchType == MatchType.SUCCESSFUL_MATCH))

    classResultOk && assocResultOk && implResultOk

  }

}

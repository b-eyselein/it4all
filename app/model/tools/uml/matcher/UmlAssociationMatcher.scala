package model.tools.uml.matcher

import model.core.matching._
import model.tools.uml.{UmlAssociation, UmlAssociationType}

final case class UmlAssociationAnalysisResult(
  endsParallel: Boolean,
  assocTypeEqual: Boolean,
  correctAssocType: UmlAssociationType,
  multiplicitiesEqual: Boolean
)

final case class UmlAssociationMatch(
  matchType: MatchType,
  userArg: Option[UmlAssociation],
  sampleArg: Option[UmlAssociation],
  maybeAnalysisResult: Option[UmlAssociationAnalysisResult]
) extends Match[UmlAssociation]

object UmlAssociationMatcher extends Matcher[UmlAssociation, UmlAssociationMatch] {

  private def endsCrossedEqual(assoc1: UmlAssociation, assoc2: UmlAssociation): Boolean =
    (assoc1.firstEnd == assoc2.secondEnd) && (assoc1.secondEnd == assoc2.firstEnd)

  def endsParallelEqual(assoc1: UmlAssociation, assoc2: UmlAssociation): Boolean =
    (assoc1.firstEnd == assoc2.firstEnd) && (assoc1.secondEnd == assoc2.secondEnd)

  override protected def canMatch(a1: UmlAssociation, a2: UmlAssociation): Boolean =
    endsParallelEqual(a1, a2) || endsCrossedEqual(a1, a2)

  override protected def instantiateOnlySampleMatch(sa: UmlAssociation): UmlAssociationMatch =
    UmlAssociationMatch(MatchType.ONLY_SAMPLE, None, Some(sa), None)

  override protected def instantiateOnlyUserMatch(ua: UmlAssociation): UmlAssociationMatch =
    UmlAssociationMatch(MatchType.ONLY_USER, Some(ua), None, None)

  override protected def instantiateCompleteMatch(ua: UmlAssociation, sa: UmlAssociation): UmlAssociationMatch = {

    val endsParallel = UmlAssociationMatcher.endsParallelEqual(ua, sa)

    val assocTypeEqual = ua.assocType == sa.assocType

    val multiplicitiesEqual = if (endsParallel) {
      ua.firstMult == sa.firstMult && ua.secondMult == sa.secondMult
    } else {
      ua.firstMult == sa.secondMult && ua.secondMult == sa.firstMult
    }

    val ar: UmlAssociationAnalysisResult =
      UmlAssociationAnalysisResult(endsParallel, assocTypeEqual, sa.assocType, multiplicitiesEqual)

    val matchType: MatchType = (assocTypeEqual, multiplicitiesEqual) match {
      case (true, true)  => MatchType.SUCCESSFUL_MATCH
      case (false, true) => MatchType.PARTIAL_MATCH
      case _             => MatchType.UNSUCCESSFUL_MATCH
    }

    UmlAssociationMatch(matchType, Some(ua), Some(sa), Some(ar))
  }
}

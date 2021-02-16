package model.tools.uml.matcher

import model.matching._
import model.tools.uml.{UmlAssociation, UmlAssociationType}

final case class UmlAssociationAnalysisResult(
  endsParallel: Boolean,
  assocTypeEqual: Boolean,
  correctAssocType: UmlAssociationType,
  multiplicitiesEqual: Boolean
)

final case class UmlAssociationMatch(
  matchType: MatchType,
  userArg: UmlAssociation,
  sampleArg: UmlAssociation,
  analysisResult: UmlAssociationAnalysisResult
) extends Match[UmlAssociation]

object UmlAssociationMatcher extends Matcher[UmlAssociation, UmlAssociationMatch] {

  private def endsCrossedEqual(assoc1: UmlAssociation, assoc2: UmlAssociation): Boolean =
    (assoc1.firstEnd == assoc2.secondEnd) && (assoc1.secondEnd == assoc2.firstEnd)

  def endsParallelEqual(assoc1: UmlAssociation, assoc2: UmlAssociation): Boolean =
    (assoc1.firstEnd == assoc2.firstEnd) && (assoc1.secondEnd == assoc2.secondEnd)

  override protected def canMatch(a1: UmlAssociation, a2: UmlAssociation): Boolean =
    endsParallelEqual(a1, a2) || endsCrossedEqual(a1, a2)

  override protected def instantiateMatch(ua: UmlAssociation, sa: UmlAssociation): UmlAssociationMatch = {

    val endsParallel = UmlAssociationMatcher.endsParallelEqual(ua, sa)

    val assocTypeEqual = ua.assocType == sa.assocType

    val multiplicitiesEqual = if (endsParallel) {
      ua.firstMult == sa.firstMult && ua.secondMult == sa.secondMult
    } else {
      ua.firstMult == sa.secondMult && ua.secondMult == sa.firstMult
    }

    val ar: UmlAssociationAnalysisResult =
      UmlAssociationAnalysisResult(endsParallel, assocTypeEqual, sa.assocType, multiplicitiesEqual)

    val matchType: MatchType = if (assocTypeEqual && multiplicitiesEqual) {
      MatchType.SUCCESSFUL_MATCH
    } else {
      MatchType.PARTIAL_MATCH
    }

    UmlAssociationMatch(matchType, ua, sa, ar)
  }
}

package model.tools.collectionTools.uml.matcher

import model.core.matching._
import model.tools.collectionTools.uml.{UmlAssociation, UmlAssociationType}

final case class UmlAssociationAnalysisResult(
  matchType: MatchType,
  endsParallel: Boolean,
  assocTypeEqual: Boolean,
  correctAssocType: UmlAssociationType,
  multiplicitiesEqual: Boolean
) extends AnalysisResult


final case class UmlAssociationMatch(
  userArg: Option[UmlAssociation],
  sampleArg: Option[UmlAssociation],
  maybeAnalysisResult: Option[UmlAssociationAnalysisResult]
) extends Match[UmlAssociation, UmlAssociationAnalysisResult]


object UmlAssociationMatcher extends Matcher[UmlAssociation, UmlAssociationAnalysisResult, UmlAssociationMatch] {

  override protected val matchName: String = "Assoziationen"

  override protected val matchSingularName: String = "der Assoziation"

  private def endsCrossedEqual(assoc1: UmlAssociation, assoc2: UmlAssociation): Boolean =
    (assoc1.firstEnd == assoc2.secondEnd) && (assoc1.secondEnd == assoc2.firstEnd)

  def endsParallelEqual(assoc1: UmlAssociation, assoc2: UmlAssociation): Boolean =
    (assoc1.firstEnd == assoc2.firstEnd) && (assoc1.secondEnd == assoc2.secondEnd)

  override protected def canMatch(a1: UmlAssociation, a2: UmlAssociation): Boolean =
    endsParallelEqual(a1, a2) || endsCrossedEqual(a1, a2)

  override protected def instantiateOnlySampleMatch(sa: UmlAssociation): UmlAssociationMatch =
    UmlAssociationMatch(None, Some(sa), None)

  override protected def instantiateOnlyUserMatch(ua: UmlAssociation): UmlAssociationMatch =
    UmlAssociationMatch(Some(ua), None, None)

  override protected def instantiateCompleteMatch(ua: UmlAssociation, sa: UmlAssociation): UmlAssociationMatch = {
    val ar: UmlAssociationAnalysisResult = {
      val endsParallel = UmlAssociationMatcher.endsParallelEqual(ua, sa)

      val assocTypeEqual = ua.assocType == sa.assocType

      val multiplicitiesEqual = if (endsParallel) {
        ua.firstMult == sa.firstMult && ua.secondMult == sa.secondMult
      } else {
        ua.firstMult == sa.secondMult && ua.secondMult == sa.firstMult
      }


      val matchType: MatchType = (assocTypeEqual, multiplicitiesEqual) match {
        case (true, true)  => MatchType.SUCCESSFUL_MATCH
        case (false, true) => MatchType.PARTIAL_MATCH
        case _             => MatchType.UNSUCCESSFUL_MATCH
      }

      UmlAssociationAnalysisResult(matchType, endsParallel, assocTypeEqual, sa.assocType, multiplicitiesEqual)
    }


    UmlAssociationMatch(Some(ua), Some(sa), Some(ar))
  }
}

package model.tools.collectionTools.uml.matcher

import model.core.matching._
import model.tools.collectionTools.uml.UmlConsts._
import model.tools.collectionTools.uml.{UmlAssociation, UmlAssociationType}
import play.api.libs.json.{JsValue, Json}

final case class UmlAssociationAnalysisResult(
  matchType: MatchType,
  endsParallel: Boolean,
  assocTypeEqual: Boolean,
  correctAssocType: UmlAssociationType,
  multiplicitiesEqual: Boolean
) extends AnalysisResult {

  override def toJson: JsValue = Json.obj(
    successName -> matchType.entryName,
    "assocTypeCorrect" -> assocTypeEqual,
    "correctAssocType" -> correctAssocType.entryName

  )

}

final case class UmlAssociationMatch(
  userArg: Option[UmlAssociation],
  sampleArg: Option[UmlAssociation],
  analysisResult: Option[UmlAssociationAnalysisResult]
) extends Match[UmlAssociation, UmlAssociationAnalysisResult] {

  /*
  override def analyze(assoc1: UmlAssociation, assoc2: UmlAssociation): UmlAssociationAnalysisResult = {
    val endsParallel = UmlAssociationMatcher.endsParallelEqual(assoc1, assoc2)

    val assocTypeEqual = assoc1.assocType == assoc2.assocType

    val multiplicitiesEqual = if (endsParallel) assoc1.firstMult == assoc2.firstMult && assoc1.secondMult == assoc2.secondMult
    else assoc1.firstMult == assoc2.secondMult && assoc1.secondMult == assoc2.firstMult


    val matchType: MatchType = (assocTypeEqual, multiplicitiesEqual) match {
      case (true, true)  => MatchType.SUCCESSFUL_MATCH
      case (false, true) => MatchType.PARTIAL_MATCH
      case _             => MatchType.UNSUCCESSFUL_MATCH
    }

    UmlAssociationAnalysisResult(matchType, endsParallel, assocTypeEqual, assoc2.assocType, multiplicitiesEqual)
  }
   */

  override protected def descArgForJson(arg: UmlAssociation): JsValue = Json.obj(
    associationTypeName -> arg.assocType.german,
    firstEndName -> arg.firstEnd, secondEndName -> arg.secondEnd,
    firstMultName -> arg.firstMult.representant, secondMultName -> arg.secondMult.representant
  )

}


object UmlAssociationMatcher extends Matcher[UmlAssociation, UmlAssociationAnalysisResult, UmlAssociationMatch] {

  override protected val matchName: String = "Assoziationen"

  override protected val matchSingularName: String = "der Assoziation"

  private def endsCrossedEqual(assoc1: UmlAssociation, assoc2: UmlAssociation): Boolean =
    (assoc1.firstEnd == assoc2.secondEnd) && (assoc1.secondEnd == assoc2.firstEnd)

  def endsParallelEqual(assoc1: UmlAssociation, assoc2: UmlAssociation): Boolean =
    (assoc1.firstEnd == assoc2.firstEnd) && (assoc1.secondEnd == assoc2.secondEnd)

  override protected def canMatch(a1: UmlAssociation, a2: UmlAssociation): Boolean =
    endsParallelEqual(a1, a2) || endsCrossedEqual(a1, a2)

  override protected def instantiatePartMatch(ua: Option[UmlAssociation], sa: Option[UmlAssociation]): UmlAssociationMatch =
    UmlAssociationMatch(ua, sa, None)

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

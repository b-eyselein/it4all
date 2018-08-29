package model.uml.matcher

import model.core.matching._
import model.uml.UmlConsts._
import model.uml.{UmlAssociation, UmlAssociationType}
import play.api.libs.json.{JsValue, Json}

case class UmlAssociationAnalysisResult(matchType: MatchType, endsParallel: Boolean,
                                        assocTypeEqual: Boolean, correctAssocType: UmlAssociationType,
                                        multiplicitiesEqual: Boolean)
  extends AnalysisResult {

  override def toJson: JsValue = Json.obj(successName -> matchType.entryName,
    "assocTypeCorrect" -> assocTypeEqual, "correctAssocType" -> correctAssocType.entryName

  )

}

case class UmlAssociationMatch(userArg: Option[UmlAssociation], sampleArg: Option[UmlAssociation]) extends Match[UmlAssociation] {

  override type AR = UmlAssociationAnalysisResult

  override def analyze(assoc1: UmlAssociation, assoc2: UmlAssociation): UmlAssociationAnalysisResult = {
    val endsParallel = UmlAssociationMatcher.endsParallelEqual(assoc1, assoc2)

    val assocTypeEqual = assoc1.assocType == assoc2.assocType

    val multiplicitiesEqual = if (endsParallel) assoc1.firstMult == assoc2.firstMult && assoc1.secondMult == assoc2.secondMult
    else assoc1.firstMult == assoc2.secondMult && assoc1.secondMult == assoc2.firstMult


    val matchType = (assocTypeEqual, multiplicitiesEqual) match {
      case (true, true)  => MatchType.SUCCESSFUL_MATCH
      case (false, true) => MatchType.PARTIAL_MATCH
      case _             => MatchType.UNSUCCESSFUL_MATCH
    }

    UmlAssociationAnalysisResult(matchType, endsParallel, assocTypeEqual, assoc2.assocType, multiplicitiesEqual)
  }

  override protected def descArgForJson(arg: UmlAssociation): JsValue = Json.obj(
    associationTypeName -> arg.assocType.german,
    firstEndName -> arg.firstEnd, secondEndName -> arg.secondEnd,
    firstMultName -> arg.firstMult.representant, secondMultName -> arg.secondMult.representant
  )

}


object UmlAssociationMatcher extends Matcher[UmlAssociation, UmlAssociationAnalysisResult, UmlAssociationMatch] {

  private def endsCrossedEqual(assoc1: UmlAssociation, assoc2: UmlAssociation): Boolean = (assoc1.firstEnd == assoc2.secondEnd) && (assoc1.secondEnd == assoc2.firstEnd)

  def endsParallelEqual(assoc1: UmlAssociation, assoc2: UmlAssociation): Boolean = (assoc1.firstEnd == assoc2.firstEnd) && (assoc1.secondEnd == assoc2.secondEnd)


  override protected def canMatch: (UmlAssociation, UmlAssociation) => Boolean = (assoc1, assoc2) => endsParallelEqual(assoc1, assoc2) || endsCrossedEqual(assoc1, assoc2)

  override protected def matchInstantiation: (Option[UmlAssociation], Option[UmlAssociation]) => UmlAssociationMatch = UmlAssociationMatch

}

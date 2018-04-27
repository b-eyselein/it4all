package model.uml.matcher

import model.core.matching._
import model.uml.UmlAssociation
import model.uml.UmlConsts._
import play.api.libs.json.{JsValue, Json}

case class UmlAssociationAnalysisResult(matchType: MatchType, endsParallel: Boolean, assocTypeEqual: Boolean, multiplicitiesEqual: Boolean)
  extends AnalysisResult {

  override def toJson: JsValue = ???

}

case class UmlAssociationMatch(userArg: Option[UmlAssociation], sampleArg: Option[UmlAssociation]) extends Match[UmlAssociation] {

  override type MatchAnalysisResult = UmlAssociationAnalysisResult

  override def analyze(assoc1: UmlAssociation, assoc2: UmlAssociation): UmlAssociationAnalysisResult = {
    val assocTypeEqual = assoc1.assocType == assoc2.assocType
    val endsParallel = UmlAssociationMatcher.endsParallelEqual(assoc1, assoc2)

    val multiplicitiesEqual = if (endsParallel) assoc1.firstMult == assoc2.firstMult && assoc1.secondMult == assoc2.secondMult
    else assoc1.firstMult == assoc2.secondMult && assoc1.secondMult == assoc2.firstMult

    val matchType = if (assocTypeEqual && multiplicitiesEqual) MatchType.SUCCESSFUL_MATCH else MatchType.UNSUCCESSFUL_MATCH

    UmlAssociationAnalysisResult(matchType, endsParallel, assocTypeEqual, multiplicitiesEqual)
  }

  var endsParallel       : Boolean = analysisResult.exists(_.endsParallel)
  var assocTypeEqual     : Boolean = analysisResult.exists(_.assocTypeEqual)
  var multiplicitiesEqual: Boolean = analysisResult.exists(_.multiplicitiesEqual)

  def displayMults(arg: UmlAssociation, turn: Boolean): String = arg.displayMult(turn)

  override protected def descArgForJson(arg: UmlAssociation): JsValue = Json.obj(
    associationTypeName -> arg.assocType.german,
    firstEndName -> arg.firstEnd, secondEndName -> arg.secondEnd,
    firstMultName -> arg.firstMult.representant, secondMultName -> arg.secondMult.representant
  )

}


object UmlAssociationMatcher extends Matcher[UmlAssociation, UmlAssociationMatch] {

  private def endsCrossedEqual(assoc1: UmlAssociation, assoc2: UmlAssociation): Boolean = (assoc1.firstEnd == assoc2.secondEnd) && (assoc1.secondEnd == assoc2.firstEnd)

  def endsParallelEqual(assoc1: UmlAssociation, assoc2: UmlAssociation): Boolean = (assoc1.firstEnd == assoc2.firstEnd) && (assoc1.secondEnd == assoc2.secondEnd)


  override protected def canMatch: (UmlAssociation, UmlAssociation) => Boolean = (assoc1, assoc2) => endsParallelEqual(assoc1, assoc2) || endsCrossedEqual(assoc1, assoc2)

  override protected def matchInstantiation: (Option[UmlAssociation], Option[UmlAssociation]) => UmlAssociationMatch = UmlAssociationMatch

}

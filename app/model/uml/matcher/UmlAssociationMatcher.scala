package model.uml.matcher

import model.core.matching.{Match, MatchType, Matcher, MatchingResult}
import model.uml.UmlAssociation
import model.uml.UmlConsts._
import play.api.libs.json.{JsValue, Json}

case class UmlAssociationMatch(userArg: Option[UmlAssociation], sampleArg: Option[UmlAssociation]) extends Match[UmlAssociation] {

  var endsParallel       : Boolean = _
  var assocTypeEqual     : Boolean = _
  var multiplicitiesEqual: Boolean = _

  override def analyze(assoc1: UmlAssociation, assoc2: UmlAssociation): MatchType = {
    assocTypeEqual = assoc1.assocType == assoc2.assocType
    endsParallel = UmlAssociationMatcher.endsParallelEqual(assoc1, assoc2)

    multiplicitiesEqual = if (endsParallel) assoc1.firstMult == assoc2.firstMult && assoc1.secondMult == assoc2.secondMult
    else assoc1.firstMult == assoc2.secondMult && assoc1.secondMult == assoc2.firstMult

    if (assocTypeEqual && multiplicitiesEqual) MatchType.SUCCESSFUL_MATCH else MatchType.UNSUCCESSFUL_MATCH
  }

  def displayMults(arg: UmlAssociation, turn: Boolean): String = arg.displayMult(turn)

  override protected def descArgForJson(arg: UmlAssociation): JsValue = Json.obj(
    associationTypeName -> arg.assocType.german,
    firstEndName -> arg.firstEnd, secondEndName -> arg.secondEnd,
    firstMultName -> arg.firstMult.representant, secondMultName -> arg.secondMult.representant
  )

}


object UmlAssociationMatcher extends Matcher[UmlAssociation, UmlAssociationMatch, UmlAssociationMatchingResult] {

  private def endsCrossedEqual(assoc1: UmlAssociation, assoc2: UmlAssociation): Boolean = (assoc1.firstEnd == assoc2.secondEnd) && (assoc1.secondEnd == assoc2.firstEnd)

  def endsParallelEqual(assoc1: UmlAssociation, assoc2: UmlAssociation): Boolean = (assoc1.firstEnd == assoc2.firstEnd) && (assoc1.secondEnd == assoc2.secondEnd)


  override protected def canMatch: (UmlAssociation, UmlAssociation) => Boolean = (assoc1, assoc2) => endsParallelEqual(assoc1, assoc2) || endsCrossedEqual(assoc1, assoc2)


  override protected def matchInstantiation: (Option[UmlAssociation], Option[UmlAssociation]) => UmlAssociationMatch = UmlAssociationMatch


  override protected def resultInstantiation: Seq[UmlAssociationMatch] => UmlAssociationMatchingResult = UmlAssociationMatchingResult

}


case class UmlAssociationMatchingResult(allMatches: Seq[UmlAssociationMatch]) extends MatchingResult[UmlAssociation, UmlAssociationMatch]

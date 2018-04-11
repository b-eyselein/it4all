package model.uml.matcher

import model.core.matching.{Matcher, MatchingResult}
import model.uml.UmlClassDiagAssociation


object UmlAssociationMatcher extends Matcher[UmlClassDiagAssociation, UmlAssociationMatch, UmlAssociationMatchingResult] {

  private def endsCrossedEqual(assoc1: UmlClassDiagAssociation, assoc2: UmlClassDiagAssociation): Boolean = (assoc1.firstEnd == assoc2.secondEnd) && (assoc1.secondEnd == assoc2.firstEnd)

  def endsParallelEqual(assoc1: UmlClassDiagAssociation, assoc2: UmlClassDiagAssociation): Boolean = (assoc1.firstEnd == assoc2.firstEnd) && (assoc1.secondEnd == assoc2.secondEnd)


  override protected def canMatch: (UmlClassDiagAssociation, UmlClassDiagAssociation) => Boolean = (assoc1, assoc2) => endsParallelEqual(assoc1, assoc2) || endsCrossedEqual(assoc1, assoc2)


  override protected def matchInstantiation: (Option[UmlClassDiagAssociation], Option[UmlClassDiagAssociation]) => UmlAssociationMatch = UmlAssociationMatch


  override protected def resultInstantiation: Seq[UmlAssociationMatch] => UmlAssociationMatchingResult = UmlAssociationMatchingResult

}


case class UmlAssociationMatchingResult(allMatches: Seq[UmlAssociationMatch]) extends MatchingResult[UmlClassDiagAssociation, UmlAssociationMatch] {

  override val matchName: String = "Assoziationen"

  //  override protected def descArg(arg: UmlClassDiagAssociation): String = describeAssociation(arg)

  //  override protected def descArgForJson(arg: UmlClassDiagAssociation): JsValue = Json.obj(
  //    "assocType" -> arg.assocType.name, "assocName" -> arg.assocName,
  //    "firstEnd" -> arg.firstEnd, "firstMult" -> arg.firstMult.representant,
  //    "secondEnd" -> arg.secondEnd, "secondMult" -> arg.secondMult.representant
  //  )


}
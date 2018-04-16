package model.uml.matcher

import model.core.matching.{Matcher, MatchingResult}
import model.uml.UmlAssociation


object UmlAssociationMatcher extends Matcher[UmlAssociation, UmlAssociationMatch, UmlAssociationMatchingResult] {

  private def endsCrossedEqual(assoc1: UmlAssociation, assoc2: UmlAssociation): Boolean = (assoc1.firstEnd == assoc2.secondEnd) && (assoc1.secondEnd == assoc2.firstEnd)

  def endsParallelEqual(assoc1: UmlAssociation, assoc2: UmlAssociation): Boolean = (assoc1.firstEnd == assoc2.firstEnd) && (assoc1.secondEnd == assoc2.secondEnd)


  override protected def canMatch: (UmlAssociation, UmlAssociation) => Boolean = (assoc1, assoc2) => endsParallelEqual(assoc1, assoc2) || endsCrossedEqual(assoc1, assoc2)


  override protected def matchInstantiation: (Option[UmlAssociation], Option[UmlAssociation]) => UmlAssociationMatch = UmlAssociationMatch


  override protected def resultInstantiation: Seq[UmlAssociationMatch] => UmlAssociationMatchingResult = UmlAssociationMatchingResult

}


case class UmlAssociationMatchingResult(allMatches: Seq[UmlAssociationMatch]) extends MatchingResult[UmlAssociation, UmlAssociationMatch]

package model.uml.matcher

import model.core.matching.{Matcher, MatchingResult}
import model.uml.UmlClassDiagClass

case class UmlClassMatcher(compareAttrsAndMethods: Boolean) extends Matcher[UmlClassDiagClass, UmlClassMatch, UmlClassMatchingResult] {

  override protected def canMatch: (UmlClassDiagClass, UmlClassDiagClass) => Boolean = _.className == _.className


  override protected def matchInstantiation: (Option[UmlClassDiagClass], Option[UmlClassDiagClass]) => UmlClassMatch = UmlClassMatch(_, _, compareAttrsAndMethods)


  override protected def resultInstantiation: Seq[UmlClassMatch] => UmlClassMatchingResult = UmlClassMatchingResult

}


case class UmlClassMatchingResult(allMatches: Seq[UmlClassMatch]) extends MatchingResult[UmlClassDiagClass, UmlClassMatch] {

  override val matchName: String = "Klassen"

}

package model.uml.matcher

import model.core.matching.{Match, Matcher, MatchingResult}
import play.api.libs.json.JsValue


case class UmlClassMemberMatch(userArg: Option[String], sampleArg: Option[String]) extends Match[String] {

  override protected def descArgForJson(arg: String): JsValue = ???

}

object UmlClassMemberMatcher extends Matcher[String, UmlClassMemberMatch, UmlClassMemberMatchingResult] {

  override protected def canMatch: (String, String) => Boolean = _ == _


  override protected def matchInstantiation: (Option[String], Option[String]) => UmlClassMemberMatch = UmlClassMemberMatch


  override protected def resultInstantiation: Seq[UmlClassMemberMatch] => UmlClassMemberMatchingResult = UmlClassMemberMatchingResult

}

case class UmlClassMemberMatchingResult(allMatches: Seq[UmlClassMemberMatch]) extends MatchingResult[String, UmlClassMemberMatch]

package model.uml.matcher

import model.Enums.MatchType
import model.core.matching.{Match, Matcher, MatchingResult}
import play.api.libs.json.JsValue


case class UmlAttributeMatch(userArg: Option[String], sampleArg: Option[String]) extends Match[String] {

  override def analyze(arg1: String, arg2: String): MatchType = super.analyze(arg1, arg2)

  override protected def descArgForJson(arg: String): JsValue = ???

}


object UmlAttributeMatcher extends Matcher[String, UmlAttributeMatch, UmlAttributeMatchingResult] {

  override protected def canMatch: (String, String) => Boolean = (ca1, ca2) => ca1 == ca2


  override protected def matchInstantiation: (Option[String], Option[String]) => UmlAttributeMatch = UmlAttributeMatch


  override protected def resultInstantiation: Seq[UmlAttributeMatch] => UmlAttributeMatchingResult = UmlAttributeMatchingResult

}

case class UmlAttributeMatchingResult(allMatches: Seq[UmlAttributeMatch]) extends MatchingResult[String, UmlAttributeMatch] {

  override val matchName: String = "Attribute"

}

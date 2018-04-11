package model.uml.matcher

import model.core.matching.{Match, Matcher, MatchingResult}
import play.api.libs.json.JsValue

case class UmlMethodMatch(userArg: Option[String], sampleArg: Option[String]) extends Match[String] {

  override protected def descArgForJson(arg: String): JsValue = ???

}


object UmlMethodsMatcher extends Matcher[String, UmlMethodMatch, UmlMethodMatchingResult] {

  override protected def canMatch: (String, String) => Boolean = _ == _


  override protected def matchInstantiation: (Option[String], Option[String]) => UmlMethodMatch = UmlMethodMatch


  override protected def resultInstantiation: Seq[UmlMethodMatch] => UmlMethodMatchingResult = UmlMethodMatchingResult

}

case class UmlMethodMatchingResult(allMatches: Seq[UmlMethodMatch]) extends MatchingResult[String, UmlMethodMatch] {

  override val matchName: String = "Methode"


}

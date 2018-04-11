package model.uml.matcher

import model.Enums.MatchType
import model.Enums.MatchType.{ONLY_SAMPLE, ONLY_USER, SUCCESSFUL_MATCH, UNSUCCESSFUL_MATCH}
import model.core.matching.Match
import model.uml._
import play.api.libs.json.JsValue

import scala.language.postfixOps

case class UmlClassMatch(userArg: Option[UmlClassDiagClass], sampleArg: Option[UmlClassDiagClass], compAM: Boolean) extends Match[UmlClassDiagClass] {

  var attributesResult: UmlAttributeMatchingResult = _
  var methodsResult   : UmlMethodMatchingResult    = _

  override def analyze(c1: UmlClassDiagClass, c2: UmlClassDiagClass): MatchType = if (!compAM) SUCCESSFUL_MATCH else {
    attributesResult = UmlAttributeMatcher.doMatch(c1.attributes, c2.attributes)
    methodsResult = UmlMethodsMatcher.doMatch(c1.methods, c2.methods)

    if (attributesResult.isSuccessful && methodsResult.isSuccessful) SUCCESSFUL_MATCH else UNSUCCESSFUL_MATCH
  }

  override def explanations: Seq[String] = matchType match {
    case MatchType.UNSUCCESSFUL_MATCH =>

      val attrExpl: String = explainMemberMatches(attributesResult.allMatches)
      val methodExpl: String = explainMemberMatches(methodsResult.allMatches)

      Seq("Es gab Fehler beim Vergleich der Member", attrExpl, methodExpl)
    case _                            => super.explanations
  }


  private def explainMemberMatches(matches: Seq[Match[String]]): String = matches filter (!_.isSuccessful) match {
    case Nil => ""
    case ms  => ms map (aMatch => aMatch.matchType match {
      case ONLY_SAMPLE => aMatch.sampleArg map (_ + " fehlte!") getOrElse "FEHLER!"
      case ONLY_USER   => aMatch.userArg map (_ + " war falsch!") getOrElse "FEHLER!"
      case _           => "FEHLER!"
    }) mkString
  }

  override protected def descArgForJson(arg: UmlClassDiagClass): JsValue = ???

}

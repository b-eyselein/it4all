package model.uml

import model.Enums.MatchType
import model.Enums.MatchType._
import model.core.EvaluationResult.PimpedHtmlString
import model.core.matching.{Match, Matcher, MatchingResult}

import scala.language.postfixOps

// Classes

case class UmlClassMatcher(compareAttrsAndMethods: Boolean) extends Matcher[UmlClassDiagClass, UmlClassMatch, UmlClassMatchingResult] {

  override def canMatch: (UmlClassDiagClass, UmlClassDiagClass) => Boolean = _.className == _.className

  override def matchInstantiation: (Option[UmlClassDiagClass], Option[UmlClassDiagClass]) => UmlClassMatch = UmlClassMatch(_, _, compareAttrsAndMethods)

  override def resultInstantiation: Seq[UmlClassMatch] => UmlClassMatchingResult = UmlClassMatchingResult

}

case class UmlClassMatch(userArg: Option[UmlClassDiagClass], sampleArg: Option[UmlClassDiagClass], compAM: Boolean) extends Match[UmlClassDiagClass] {

  var attributesResult: UmlAttributeMatchingResult = _
  var methodsResult   : UmlMethodMatchingResult    = _

  override def analyze(c1: UmlClassDiagClass, c2: UmlClassDiagClass): MatchType = if (!compAM) SUCCESSFUL_MATCH else {
    attributesResult = UmlAttributeMatcher.doMatch(c1.attributes, c2.attributes)
    methodsResult = UmlMethodsMatcher.doMatch(c1.methods, c2.methods)

    if (attributesResult.isSuccessful && methodsResult.isSuccessful) SUCCESSFUL_MATCH else UNSUCCESSFUL_MATCH
  }

  override def explanation: String = matchType match {
    case MatchType.UNSUCCESSFUL_MATCH =>

      val attrExpl: String = explainMemberMatches(attributesResult.allMatches)
      val methodExpl: String = explainMemberMatches(methodsResult.allMatches)

      "Es gab Fehler beim Vergleich der Member: <ul>" + attrExpl + methodExpl + "</ul>"
    case _                            => super.explanation
  }

  override protected def descArg(arg: UmlClassDiagClass): String = arg.className

  private def explainMemberMatches(matches: Seq[Match[_ <: UmlClassDiagClassMember]]): String = matches filter (!_.isSuccessful) match {
    case Nil => ""
    case ms  => ms map (aMatch => aMatch.matchType match {
      case ONLY_SAMPLE => aMatch.sampleArg map (_.render.asCode + " fehlte!") getOrElse "FEHLER!"
      case ONLY_USER   => aMatch.userArg map (_.render.asCode + " war falsch!") getOrElse "FEHLER!"
      case _           => "FEHLER!"
    }) map (_.asListElem) mkString
  }

}

case class UmlClassMatchingResult(allMatches: Seq[UmlClassMatch]) extends MatchingResult[UmlClassDiagClass, UmlClassMatch] {

  override val matchName: String = "Klassen"

}

// Uml class attributes

object UmlAttributeMatcher extends Matcher[UmlClassDiagClassAttribute, UmlAttributeMatch, UmlAttributeMatchingResult] {

  override def canMatch: (UmlClassDiagClassAttribute, UmlClassDiagClassAttribute) => Boolean =
    (ca1, ca2) => ca1.name == ca2.name && ca1.memberType == ca2.memberType

  override def matchInstantiation: (Option[UmlClassDiagClassAttribute], Option[UmlClassDiagClassAttribute]) => UmlAttributeMatch = UmlAttributeMatch

  override def resultInstantiation: Seq[UmlAttributeMatch] => UmlAttributeMatchingResult = UmlAttributeMatchingResult

}

case class UmlAttributeMatch(userArg: Option[UmlClassDiagClassAttribute], sampleArg: Option[UmlClassDiagClassAttribute]) extends Match[UmlClassDiagClassAttribute] {

  override def analyze(arg1: UmlClassDiagClassAttribute, arg2: UmlClassDiagClassAttribute): MatchType = super.analyze(arg1, arg2)

}

case class UmlAttributeMatchingResult(allMatches: Seq[UmlAttributeMatch]) extends MatchingResult[UmlClassDiagClassAttribute, UmlAttributeMatch] {

  override val matchName: String = "Attribute"

}

// Uml class methods

object UmlMethodsMatcher extends Matcher[UmlClassDiagClassMethod, UmlMethodMatch, UmlMethodMatchingResult] {

  override def canMatch: (UmlClassDiagClassMethod, UmlClassDiagClassMethod) => Boolean =
    (m1, m2) => m1.name == m2.name && m1.memberType == m2.memberType

  override def matchInstantiation: (Option[UmlClassDiagClassMethod], Option[UmlClassDiagClassMethod]) => UmlMethodMatch = UmlMethodMatch

  override def resultInstantiation: Seq[UmlMethodMatch] => UmlMethodMatchingResult = UmlMethodMatchingResult

}

case class UmlMethodMatch(userArg: Option[UmlClassDiagClassMethod], sampleArg: Option[UmlClassDiagClassMethod]) extends Match[UmlClassDiagClassMethod] {

  override def analyze(arg1: UmlClassDiagClassMethod, arg2: UmlClassDiagClassMethod): MatchType = super.analyze(arg1, arg2)

}

case class UmlMethodMatchingResult(allMatches: Seq[UmlMethodMatch]) extends MatchingResult[UmlClassDiagClassMethod, UmlMethodMatch] {

  override val matchName: String = "Methode"

}
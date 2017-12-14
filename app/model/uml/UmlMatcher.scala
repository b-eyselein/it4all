package model.uml

import model.Enums.MatchType
import model.Enums.MatchType._
import model.core.EvaluationResult.PimpedHtmlString
import model.core.matching.{Match, Matcher, MatchingResult}

import scala.language.postfixOps

// Classes

case class UmlClassMatcher(compareAttrsAndMethods: Boolean) extends Matcher[UmlCompleteClass, UmlClassMatch, UmlClassMatchingResult] {

  override def canMatch: (UmlCompleteClass, UmlCompleteClass) => Boolean = _.clazz.className == _.clazz.className

  override def matchInstantiation: (Option[UmlCompleteClass], Option[UmlCompleteClass]) => UmlClassMatch = UmlClassMatch(_, _, compareAttrsAndMethods)

  override def resultInstantiation: Seq[UmlClassMatch] => UmlClassMatchingResult = UmlClassMatchingResult

}

case class UmlClassMatch(userArg: Option[UmlCompleteClass], sampleArg: Option[UmlCompleteClass], compAM: Boolean) extends Match[UmlCompleteClass] {

  var attributesResult: UmlAttributeMatchingResult = _
  var methodsResult   : UmlMethodMatchingResult    = _

  override def analyze(c1: UmlCompleteClass, c2: UmlCompleteClass): MatchType = if (!compAM) SUCCESSFUL_MATCH else {
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

  override protected def descArg(arg: UmlCompleteClass): String = arg.clazz.className

  private def explainMemberMatches(matches: Seq[Match[_ <: UmlClassMember]]): String = matches filter (!_.isSuccessful) match {
    case Nil => ""
    case ms  => ms map (aMatch => aMatch.matchType match {
      case ONLY_SAMPLE => aMatch.sampleArg map (_.render.asCode + " fehlte!") getOrElse "FEHLER!"
      case ONLY_USER   => aMatch.userArg map (_.render.asCode + " war falsch!") getOrElse "FEHLER!"
    }) map (_.asListElem) mkString
  }

}

case class UmlClassMatchingResult(allMatches: Seq[UmlClassMatch]) extends MatchingResult[UmlCompleteClass, UmlClassMatch] {

  override val matchName: String = "Klassen"

}

// Uml class attributes

object UmlAttributeMatcher extends Matcher[UmlClassAttribute, UmlAttributeMatch, UmlAttributeMatchingResult] {

  override def canMatch: (UmlClassAttribute, UmlClassAttribute) => Boolean = (ca1, ca2) => ca1.name == ca2.name && ca1.umlType == ca2.umlType

  override def matchInstantiation: (Option[UmlClassAttribute], Option[UmlClassAttribute]) => UmlAttributeMatch = UmlAttributeMatch

  override def resultInstantiation: Seq[UmlAttributeMatch] => UmlAttributeMatchingResult = UmlAttributeMatchingResult

}

case class UmlAttributeMatch(userArg: Option[UmlClassAttribute], sampleArg: Option[UmlClassAttribute]) extends Match[UmlClassAttribute] {

  override def analyze(arg1: UmlClassAttribute, arg2: UmlClassAttribute): MatchType = super.analyze(arg1, arg2)

  override def explanation: String = {
    println(this.matchType)
    super.explanation
  }

}

case class UmlAttributeMatchingResult(allMatches: Seq[UmlAttributeMatch]) extends MatchingResult[UmlClassAttribute, UmlAttributeMatch] {

  override val matchName: String = "Attribute"

}

// Uml class methods

object UmlMethodsMatcher extends Matcher[UmlClassMethod, UmlMethodMatch, UmlMethodMatchingResult] {

  override def canMatch: (UmlClassMethod, UmlClassMethod) => Boolean = (m1, m2) => m1.name == m2.name && m1.umlType == m2.umlType

  override def matchInstantiation: (Option[UmlClassMethod], Option[UmlClassMethod]) => UmlMethodMatch = UmlMethodMatch

  override def resultInstantiation: Seq[UmlMethodMatch] => UmlMethodMatchingResult = UmlMethodMatchingResult

}

case class UmlMethodMatch(userArg: Option[UmlClassMethod], sampleArg: Option[UmlClassMethod]) extends Match[UmlClassMethod] {

  override def analyze(arg1: UmlClassMethod, arg2: UmlClassMethod): MatchType = super.analyze(arg1, arg2)

}

case class UmlMethodMatchingResult(allMatches: Seq[UmlMethodMatch]) extends MatchingResult[UmlClassMethod, UmlMethodMatch] {

  override val matchName: String = "Methode"

}
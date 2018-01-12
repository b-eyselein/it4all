package model.uml

import model.Enums.MatchType
import model.Enums.MatchType.{ONLY_SAMPLE, ONLY_USER, SUCCESSFUL_MATCH, UNSUCCESSFUL_MATCH}
import model.core.EvaluationResult.PimpedHtmlString
import model.core.matching.{Match, Matcher, MatchingResult}
import model.uml.UmlResult.{describeAssociation, describeImplementation}
import play.twirl.api.Html

import scala.language.postfixOps

// Implementations

object UmlImplementationMatcher extends Matcher[UmlImplementation, UmlImplementationMatch, UmlImplementationMatchingResult] {

  override def canMatch: (UmlImplementation, UmlImplementation) => Boolean = (i1, i2) =>
    (i1.subClass == i2.subClass && i1.superClass == i2.superClass) || (i1.subClass == i2.superClass && i1.superClass == i2.subClass)

  override def matchInstantiation: (Option[UmlImplementation], Option[UmlImplementation]) => UmlImplementationMatch = UmlImplementationMatch

  override def resultInstantiation: Seq[UmlImplementationMatch] => UmlImplementationMatchingResult = UmlImplementationMatchingResult

}

case class UmlImplementationMatch(userArg: Option[UmlImplementation], sampleArg: Option[UmlImplementation]) extends Match[UmlImplementation] {

  override def analyze(i1: UmlImplementation, i2: UmlImplementation): MatchType =
    if (i1.subClass == i2.subClass && i1.superClass == i2.superClass) SUCCESSFUL_MATCH else UNSUCCESSFUL_MATCH

  override def explanation: String = matchType match {
    case UNSUCCESSFUL_MATCH => "Vererbungsrichtung falsch."
    case ONLY_SAMPLE        => "Vererbungsbeziehung nicht erstellt."
    case ONLY_USER          => "Vererbengsbeziehung ist falsch."
    case _                  => super.explanation
  }

  override def descArg(arg: UmlImplementation): String = describeImplementation(arg)

}

case class UmlImplementationMatchingResult(allMatches: Seq[UmlImplementationMatch]) extends MatchingResult[UmlImplementation, UmlImplementationMatch] {

  override val matchName: String = "Vererbungsbeziehungen"

  // FIXME: override with list instead of join with ", "
  override def describe: Html = super.describe

}

// Associations

object UmlAssociationMatcher extends Matcher[UmlAssociation, UmlAssociationMatch, UmlAssociationMatchingResult] {

  override def canMatch: (UmlAssociation, UmlAssociation) => Boolean = (assoc1, assoc2) => endsParallelEqual(assoc1, assoc2) || endsCrossedEqual(assoc1, assoc2)

  private def endsCrossedEqual(assoc1: UmlAssociation, assoc2: UmlAssociation): Boolean = (assoc1.firstEnd == assoc2.secondEnd) && (assoc1.secondEnd == assoc2.firstEnd)

  def endsParallelEqual(assoc1: UmlAssociation, assoc2: UmlAssociation): Boolean = (assoc1.firstEnd == assoc2.firstEnd) && (assoc1.secondEnd == assoc2.secondEnd)

  override def matchInstantiation: (Option[UmlAssociation], Option[UmlAssociation]) => UmlAssociationMatch = UmlAssociationMatch

  override def resultInstantiation: Seq[UmlAssociationMatch] => UmlAssociationMatchingResult = UmlAssociationMatchingResult

}

case class UmlAssociationMatch(userArg: Option[UmlAssociation], sampleArg: Option[UmlAssociation]) extends Match[UmlAssociation] {

  var endsParallel       : Boolean = _
  var assocTypeEqual     : Boolean = _
  var multiplicitiesEqual: Boolean = _

  override def analyze(assoc1: UmlAssociation, assoc2: UmlAssociation): MatchType = {
    assocTypeEqual = assoc1.assocType == assoc2.assocType
    endsParallel = UmlAssociationMatcher.endsParallelEqual(assoc1, assoc2)

    multiplicitiesEqual =
      if (endsParallel) assoc1.firstMult == assoc2.firstMult && assoc1.secondMult == assoc2.secondMult
      else assoc1.firstMult == assoc2.secondMult && assoc1.secondMult == assoc2.firstMult

    if (assocTypeEqual && multiplicitiesEqual) SUCCESSFUL_MATCH
    else UNSUCCESSFUL_MATCH
  }

  def displayMults(arg: UmlAssociation, turn: Boolean): String = arg.displayMult(turn)

  override def explanation: String = matchType match {
    case UNSUCCESSFUL_MATCH =>
      val multExpl = if (multiplicitiesEqual) ""
      else "Multiplizitäten sind falsch, erwartet war " + (sampleArg map (sa => displayMults(sa, turn = !endsParallel) asCode) getOrElse "FEHLER!") + " ."

      val assocExpl = if (assocTypeEqual) ""
      else "Assoziationstyp ist falsch, erwartet war " + (sampleArg map (_.assocType.germanName asCode) getOrElse "FEHLER!") + "."

      multExpl + assocExpl
    case _                  => super.explanation
  }

  override protected def descArg(arg: UmlAssociation): String = describeAssociation(arg)

}

case class UmlAssociationMatchingResult(allMatches: Seq[UmlAssociationMatch]) extends MatchingResult[UmlAssociation, UmlAssociationMatch] {

  override val matchName: String = "Assoziationen"

}

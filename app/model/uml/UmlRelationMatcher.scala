package model.uml

import model.Enums.MatchType
import model.Enums.MatchType.{ONLY_SAMPLE, ONLY_USER, SUCCESSFUL_MATCH, UNSUCCESSFUL_MATCH}
import model.core.EvaluationResult.PimpedHtmlString
import model.core.matching.{Match, Matcher, MatchingResult}
import model.uml.UmlCompleteResult.{describeAssociation, describeImplementation}

import scala.language.postfixOps

// Implementations

object UmlImplementationMatcher extends Matcher[UmlClassDiagImplementation, UmlImplementationMatch, UmlImplementationMatchingResult] {

  override def canMatch: (UmlClassDiagImplementation, UmlClassDiagImplementation) => Boolean = (i1, i2) =>
    (i1.subClass == i2.subClass && i1.superClass == i2.superClass) || (i1.subClass == i2.superClass && i1.superClass == i2.subClass)

  override def matchInstantiation: (Option[UmlClassDiagImplementation], Option[UmlClassDiagImplementation]) => UmlImplementationMatch = UmlImplementationMatch

  override def resultInstantiation: Seq[UmlImplementationMatch] => UmlImplementationMatchingResult = UmlImplementationMatchingResult

}

case class UmlImplementationMatch(userArg: Option[UmlClassDiagImplementation], sampleArg: Option[UmlClassDiagImplementation]) extends Match[UmlClassDiagImplementation] {

  override def analyze(i1: UmlClassDiagImplementation, i2: UmlClassDiagImplementation): MatchType =
    if (i1.subClass == i2.subClass && i1.superClass == i2.superClass) SUCCESSFUL_MATCH else UNSUCCESSFUL_MATCH

  override def explanation: String = matchType match {
    case UNSUCCESSFUL_MATCH => "Vererbungsrichtung falsch."
    case ONLY_SAMPLE        => "Vererbungsbeziehung nicht erstellt."
    case ONLY_USER          => "Vererbengsbeziehung ist falsch."
    case _                  => super.explanation
  }

  override def descArg(arg: UmlClassDiagImplementation): String = describeImplementation(arg)

}

case class UmlImplementationMatchingResult(allMatches: Seq[UmlImplementationMatch]) extends MatchingResult[UmlClassDiagImplementation, UmlImplementationMatch] {

  override val matchName: String = "Vererbungsbeziehungen"

  // FIXME: override with list instead of join with ", "
  //  override def describe: String = super.describe

}

// Associations

object UmlAssociationMatcher extends Matcher[UmlClassDiagAssociation, UCD_AssociationMatch, UCD_AssociationMatchingResult] {

  override def canMatch: (UmlClassDiagAssociation, UmlClassDiagAssociation) => Boolean = (assoc1, assoc2) => endsParallelEqual(assoc1, assoc2) || endsCrossedEqual(assoc1, assoc2)

  private def endsCrossedEqual(assoc1: UmlClassDiagAssociation, assoc2: UmlClassDiagAssociation): Boolean = (assoc1.firstEnd == assoc2.secondEnd) && (assoc1.secondEnd == assoc2.firstEnd)

  def endsParallelEqual(assoc1: UmlClassDiagAssociation, assoc2: UmlClassDiagAssociation): Boolean = (assoc1.firstEnd == assoc2.firstEnd) && (assoc1.secondEnd == assoc2.secondEnd)

  override def matchInstantiation: (Option[UmlClassDiagAssociation], Option[UmlClassDiagAssociation]) => UCD_AssociationMatch = UCD_AssociationMatch

  override def resultInstantiation: Seq[UCD_AssociationMatch] => UCD_AssociationMatchingResult = UCD_AssociationMatchingResult

}

case class UCD_AssociationMatch(userArg: Option[UmlClassDiagAssociation], sampleArg: Option[UmlClassDiagAssociation]) extends Match[UmlClassDiagAssociation] {

  var endsParallel       : Boolean = _
  var assocTypeEqual     : Boolean = _
  var multiplicitiesEqual: Boolean = _

  override def analyze(assoc1: UmlClassDiagAssociation, assoc2: UmlClassDiagAssociation): MatchType = {
    assocTypeEqual = assoc1.assocType == assoc2.assocType
    endsParallel = UmlAssociationMatcher.endsParallelEqual(assoc1, assoc2)

    multiplicitiesEqual =
      if (endsParallel) assoc1.firstMult == assoc2.firstMult && assoc1.secondMult == assoc2.secondMult
      else assoc1.firstMult == assoc2.secondMult && assoc1.secondMult == assoc2.firstMult

    if (assocTypeEqual && multiplicitiesEqual) SUCCESSFUL_MATCH
    else UNSUCCESSFUL_MATCH
  }

  def displayMults(arg: UmlClassDiagAssociation, turn: Boolean): String = arg.displayMult(turn)

  override def explanation: String = matchType match {
    case UNSUCCESSFUL_MATCH =>
      val multExpl = if (multiplicitiesEqual) ""
      else "Multiplizitäten sind falsch, erwartet war " + (sampleArg map (sa => displayMults(sa, turn = !endsParallel) asCode) getOrElse "FEHLER!") + " ."

      val assocExpl = if (assocTypeEqual) ""
      else "Assoziationstyp ist falsch, erwartet war " + (sampleArg map (_.assocType.germanName asCode) getOrElse "FEHLER!") + "."

      multExpl + assocExpl
    case _                  => super.explanation
  }

  override protected def descArg(arg: UmlClassDiagAssociation): String = describeAssociation(arg)

}

case class UCD_AssociationMatchingResult(allMatches: Seq[UCD_AssociationMatch]) extends MatchingResult[UmlClassDiagAssociation, UCD_AssociationMatch] {

  override val matchName: String = "Assoziationen"

}

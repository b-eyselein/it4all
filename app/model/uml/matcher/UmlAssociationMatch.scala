package model.uml.matcher

import model.Enums.MatchType
import model.Enums.MatchType.{ONLY_SAMPLE, ONLY_USER, SUCCESSFUL_MATCH, UNSUCCESSFUL_MATCH}
import model.core.matching.Match
import model.uml.UmlClassDiagAssociation
import model.uml.UmlConsts._
import play.api.libs.json.{JsValue, Json}


case class UmlAssociationMatch(userArg: Option[UmlClassDiagAssociation], sampleArg: Option[UmlClassDiagAssociation]) extends Match[UmlClassDiagAssociation] {

  var endsParallel       : Boolean = _
  var assocTypeEqual     : Boolean = _
  var multiplicitiesEqual: Boolean = _

  override def analyze(assoc1: UmlClassDiagAssociation, assoc2: UmlClassDiagAssociation): MatchType = {
    assocTypeEqual = assoc1.assocType == assoc2.assocType
    endsParallel = UmlAssociationMatcher.endsParallelEqual(assoc1, assoc2)

    multiplicitiesEqual = if (endsParallel) assoc1.firstMult == assoc2.firstMult && assoc1.secondMult == assoc2.secondMult
    else assoc1.firstMult == assoc2.secondMult && assoc1.secondMult == assoc2.firstMult

    if (assocTypeEqual && multiplicitiesEqual) SUCCESSFUL_MATCH else UNSUCCESSFUL_MATCH
  }

  def displayMults(arg: UmlClassDiagAssociation, turn: Boolean): String = arg.displayMult(turn)

  override def explanations: Seq[String] = matchType match {
    case ONLY_SAMPLE => Seq("Die Assoziationsbeziehung wurde nicht erstellt, war aber in der Musterlösung vorhanden.")
    case ONLY_USER   => Seq("Die Assoziationsbeziehung wurde erstellt, ist aber nicht in der Musterlösung vorhanden.")

    case UNSUCCESSFUL_MATCH =>
      val multExpl = if (multiplicitiesEqual) None
      else Some("Die Multiplizität <code>" + (userArg map (ua => displayMults(ua, turn = false)) getOrElse "FEHLER!") + "</code> ist falsch, erwartet war <code>" +
        (sampleArg map (sa => displayMults(sa, turn = !endsParallel)) getOrElse "FEHLER!") + "</code>!")

      val assocExpl = if (assocTypeEqual) None
      else Some("Der Assoziationstyp <code>" + (userArg map (ua => ua.assocType.germanName) getOrElse "FEHLER") + "</code> ist falsch, erwartet war <code>" +
        (sampleArg map (a => a.assocType.germanName) getOrElse "FEHLER!") + "</code>")

      Seq.empty ++ multExpl ++ assocExpl

    case _ => super.explanations
  }

  override protected def descArgForJson(arg: UmlClassDiagAssociation): JsValue = Json.obj(
    firstEndName -> arg.firstEnd, secondEndName -> arg.secondEnd, firstMultName -> arg.firstMult.representant, secondMultName -> arg.secondMult.representant
  )

}
package model.uml.matcher

import model.Enums.MatchType
import model.Enums.MatchType._
import model.core.matching._
import model.uml.UmlClassDiagImplementation
import model.uml.UmlCompleteResult.describeImplementation
import model.uml.UmlConsts._
import play.api.libs.json.{JsValue, Json}

case class UmlImplementationMatch(userArg: Option[UmlClassDiagImplementation], sampleArg: Option[UmlClassDiagImplementation]) extends Match[UmlClassDiagImplementation] {

  override def analyze(i1: UmlClassDiagImplementation, i2: UmlClassDiagImplementation): MatchType =
    if (i1.subClass == i2.subClass && i1.superClass == i2.superClass) SUCCESSFUL_MATCH else PARTIAL_MATCH

  override def explanations: Seq[String] = matchType match {
    case SUCCESSFUL_MATCH                     => Seq.empty
    case (UNSUCCESSFUL_MATCH | PARTIAL_MATCH) => Seq("Vererbungsrichtung falsch.")
    case ONLY_SAMPLE                          => Seq("Vererbungsbeziehung nicht erstellt.")
    case ONLY_USER                            => Seq("Vererbengsbeziehung ist falsch.")
    case FAILURE                              => Seq("Es gab einen internen Fehler bei der Korrektur der Vererbungsbeziehungen!")
  }

  override def descArg(arg: UmlClassDiagImplementation): String = describeImplementation(arg)

  override protected def descArgForJson(arg: UmlClassDiagImplementation): JsValue = Json.obj(
    subClassName -> arg.subClass, superClassName -> arg.superClass
  )

}


object UmlImplementationMatcher extends Matcher[UmlClassDiagImplementation, UmlImplementationMatch, UmlImplementationMatchingResult] {

  override protected def canMatch: (UmlClassDiagImplementation, UmlClassDiagImplementation) => Boolean = (i1, i2) =>
    (i1.subClass == i2.subClass && i1.superClass == i2.superClass) || (i1.subClass == i2.superClass && i1.superClass == i2.subClass)


  override protected def matchInstantiation: (Option[UmlClassDiagImplementation], Option[UmlClassDiagImplementation]) => UmlImplementationMatch = UmlImplementationMatch


  override protected def resultInstantiation: Seq[UmlImplementationMatch] => UmlImplementationMatchingResult = UmlImplementationMatchingResult
}


case class UmlImplementationMatchingResult(allMatches: Seq[UmlImplementationMatch]) extends MatchingResult[UmlClassDiagImplementation, UmlImplementationMatch] {

  override val matchName: String = "Vererbungsbeziehungen"

}


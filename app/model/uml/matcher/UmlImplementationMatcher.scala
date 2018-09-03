package model.uml.matcher

import model.core.matching._
import model.uml.UmlCompleteResult.describeImplementation
import model.uml.UmlConsts._
import model.uml.UmlImplementation
import play.api.libs.json.{JsValue, Json}

final case class UmlImplementationMatch(userArg: Option[UmlImplementation], sampleArg: Option[UmlImplementation]) extends Match[UmlImplementation] {

  override type AR = GenericAnalysisResult

  override def analyze(i1: UmlImplementation, i2: UmlImplementation): GenericAnalysisResult =
    GenericAnalysisResult(if (i1.subClass == i2.subClass && i1.superClass == i2.superClass) MatchType.SUCCESSFUL_MATCH else MatchType.PARTIAL_MATCH)

  override def explanations: Seq[String] = matchType match {
    case MatchType.SUCCESSFUL_MATCH                             => Seq[String]()
    case MatchType.UNSUCCESSFUL_MATCH | MatchType.PARTIAL_MATCH => Seq("Vererbungsrichtung falsch.")
    case MatchType.ONLY_SAMPLE                                  => Seq("Vererbungsbeziehung nicht erstellt.")
    case MatchType.ONLY_USER                                    => Seq("Vererbengsbeziehung ist falsch.")
  }

  override def descArg(arg: UmlImplementation): String = describeImplementation(arg)

  override protected def descArgForJson(arg: UmlImplementation): JsValue = Json.obj(
    subClassName -> arg.subClass, superClassName -> arg.superClass
  )

}


object UmlImplementationMatcher extends Matcher[UmlImplementation, GenericAnalysisResult, UmlImplementationMatch] {

  override protected def canMatch: (UmlImplementation, UmlImplementation) => Boolean = (i1, i2) =>
    (i1.subClass == i2.subClass && i1.superClass == i2.superClass) || (i1.subClass == i2.superClass && i1.superClass == i2.subClass)


  override protected def matchInstantiation: (Option[UmlImplementation], Option[UmlImplementation]) => UmlImplementationMatch = UmlImplementationMatch

}

package model.tools.uml.matcher

import model.core.matching._
import model.tools.uml.UmlCompleteResult.describeImplementation
import model.tools.uml.UmlConsts._
import model.tools.uml.UmlImplementation
import play.api.libs.json.{JsValue, Json}

final case class UmlImplementationMatch(userArg: Option[UmlImplementation], sampleArg: Option[UmlImplementation]) extends Match {

  override type T = UmlImplementation

  override type AR = GenericAnalysisResult

  override def analyze(i1: UmlImplementation, i2: UmlImplementation): GenericAnalysisResult =
    GenericAnalysisResult(if (i1.subClass == i2.subClass && i1.superClass == i2.superClass) MatchType.SUCCESSFUL_MATCH else MatchType.PARTIAL_MATCH)

  override def descArg(arg: UmlImplementation): String = describeImplementation(arg)

  override protected def descArgForJson(arg: UmlImplementation): JsValue = Json.obj(
    subClassName -> arg.subClass, superClassName -> arg.superClass
  )

}


object UmlImplementationMatcher extends Matcher[UmlImplementationMatch] {

  override type T = UmlImplementation

  override protected val matchName: String = "Vererbungen"

  override protected val matchSingularName: String = "der Vererbung"

  override protected def canMatch(i1: UmlImplementation, i2: UmlImplementation): Boolean =
    (i1.subClass == i2.subClass && i1.superClass == i2.superClass) || (i1.subClass == i2.superClass && i1.superClass == i2.subClass)

  override protected def matchInstantiation(ua: Option[UmlImplementation], sa: Option[UmlImplementation]): UmlImplementationMatch =
    UmlImplementationMatch(ua, sa)

}

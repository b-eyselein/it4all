package model.uml.matcher

import model.core.matching._
import model.uml.UmlConsts._
import model.uml.{UmlAttribute, UmlClass, UmlMethod}
import play.api.libs.json.{JsValue, Json}

import scala.language.postfixOps

case class UmlClassMatchAnalysisResult(matchType: MatchType,
                                       maybeAttributeMatchingResult: Option[MatchingResult[UmlAttribute, UmlAttributeMatch]],
                                       maybeMethodMatchingResult: Option[MatchingResult[UmlMethod, UmlMethodMatch]])
  extends AnalysisResult {

  override def toJson: JsValue = Json.obj(
    successName -> matchType.entryName,
    attributesResultName -> maybeAttributeMatchingResult.map(_.toJson),
    methodsResultName -> maybeMethodMatchingResult.map(_.toJson)
  )

}


case class UmlClassMatch(userArg: Option[UmlClass], sampleArg: Option[UmlClass], compAM: Boolean) extends Match[UmlClass] {

  override type MatchAnalysisResult = UmlClassMatchAnalysisResult

  def analyze(c1: UmlClass, c2: UmlClass): UmlClassMatchAnalysisResult = if (compAM) {
    val attrResult: MatchingResult[UmlAttribute, UmlAttributeMatch] = UmlAttributeMatcher.doMatch(c1.attributes, c2.attributes)
    val methResult = UmlMethodMatcher.doMatch(c1.methods, c2.methods)

    val matchType = if (attrResult.isSuccessful && methResult.isSuccessful) MatchType.SUCCESSFUL_MATCH else MatchType.UNSUCCESSFUL_MATCH

    UmlClassMatchAnalysisResult(matchType, Some(attrResult), Some(methResult))
  } else {
    UmlClassMatchAnalysisResult(MatchType.SUCCESSFUL_MATCH, None, None)
  }

  // FIXME: check if correct!
  override protected def descArgForJson(arg: UmlClass): JsValue = Json.obj(classNameName -> arg.className, classTypeName -> arg.classType.entryName)

  val attributesResult: Option[MatchingResult[UmlAttribute, UmlAttributeMatch]] = analysisResult.flatMap(_.maybeAttributeMatchingResult)
  val methodsResult   : Option[MatchingResult[UmlMethod, UmlMethodMatch]]       = analysisResult.flatMap(_.maybeMethodMatchingResult)

}


case class UmlClassMatcher(compareAttrsAndMethods: Boolean) extends Matcher[UmlClass, UmlClassMatch] {

  override protected def canMatch: (UmlClass, UmlClass) => Boolean = _.className == _.className

  override protected def matchInstantiation: (Option[UmlClass], Option[UmlClass]) => UmlClassMatch = UmlClassMatch(_, _, compareAttrsAndMethods)

}

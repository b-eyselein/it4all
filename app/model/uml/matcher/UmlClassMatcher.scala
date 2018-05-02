package model.uml.matcher

import model.core.matching._
import model.uml.UmlConsts._
import model.uml.{UmlAttribute, UmlClass, UmlClassType, UmlMethod}
import play.api.libs.json.{JsValue, Json}

import scala.language.postfixOps

case class UmlClassMatchAnalysisResult(matchType: MatchType, classTypeCorrect: Boolean, correctClassType: UmlClassType,
                                       maybeAttributeMatchingResult: Option[MatchingResult[UmlAttribute, UmlAttributeMatch]],
                                       maybeMethodMatchingResult: Option[MatchingResult[UmlMethod, UmlMethodMatch]])
  extends AnalysisResult {

  override def toJson: JsValue = Json.obj(
    successName -> matchType.entryName,
    "classTypeCorrect" -> classTypeCorrect, "correctClassType" -> correctClassType.german,
    attributesResultName -> maybeAttributeMatchingResult.map(_.toJson),
    methodsResultName -> maybeMethodMatchingResult.map(_.toJson)
  )

}


case class UmlClassMatch(userArg: Option[UmlClass], sampleArg: Option[UmlClass], compAM: Boolean) extends Match[UmlClass] {

  override type MatchAnalysisResult = UmlClassMatchAnalysisResult

  def analyze(c1: UmlClass, c2: UmlClass): UmlClassMatchAnalysisResult = {
    val classTypeCorrect = c1.classType == c2.classType

    if (compAM) {
      val attributesResult: MatchingResult[UmlAttribute, UmlAttributeMatch] = UmlAttributeMatcher.doMatch(c1.attributes, c2.attributes)
      val methodsResult = UmlMethodMatcher.doMatch(c1.methods, c2.methods)

      val membersCorrect = attributesResult.isSuccessful && methodsResult.isSuccessful

      val matchType = (classTypeCorrect, membersCorrect) match {
        case (true, true)  => MatchType.SUCCESSFUL_MATCH
        case (false, true) => MatchType.PARTIAL_MATCH
        case _             => MatchType.UNSUCCESSFUL_MATCH
      }

      UmlClassMatchAnalysisResult(matchType, classTypeCorrect, c2.classType, Some(attributesResult), Some(methodsResult))
    } else {
      UmlClassMatchAnalysisResult(MatchType.SUCCESSFUL_MATCH, classTypeCorrect, c2.classType, None, None)
    }
  }

  // FIXME: check if correct!
  override protected def descArgForJson(arg: UmlClass): JsValue = Json.obj(nameName -> arg.className, classTypeName -> arg.classType.entryName)

  val attributesResult: Option[MatchingResult[UmlAttribute, UmlAttributeMatch]] = analysisResult.flatMap(_.maybeAttributeMatchingResult)
  val methodsResult   : Option[MatchingResult[UmlMethod, UmlMethodMatch]]       = analysisResult.flatMap(_.maybeMethodMatchingResult)

}


case class UmlClassMatcher(compareAttrsAndMethods: Boolean) extends Matcher[UmlClass, UmlClassMatch] {

  override protected def canMatch: (UmlClass, UmlClass) => Boolean = _.className == _.className

  override protected def matchInstantiation: (Option[UmlClass], Option[UmlClass]) => UmlClassMatch = UmlClassMatch(_, _, compareAttrsAndMethods)

}

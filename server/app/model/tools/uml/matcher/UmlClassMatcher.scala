package model.tools.uml.matcher

import model.core.matching._
import model.core.result.SuccessType
import model.tools.uml.UmlConsts._
import model.tools.uml.{UmlClass, UmlClassType}
import play.api.libs.json.{JsValue, Json}

final case class UmlClassMatchAnalysisResult(matchType: MatchType, classTypeCorrect: Boolean, correctClassType: UmlClassType,
                                             maybeAttributeMatchingResult: Option[MatchingResult[UmlAttributeMatch]],
                                             maybeMethodMatchingResult: Option[MatchingResult[UmlMethodMatch]])
  extends AnalysisResult {

  override def toJson: JsValue = Json.obj(
    successName -> matchType.entryName,
    "classTypeCorrect" -> classTypeCorrect, "correctClassType" -> correctClassType.german,
    attributesResultName -> maybeAttributeMatchingResult.map(_.toJson),
    methodsResultName -> maybeMethodMatchingResult.map(_.toJson)
  )

}


final case class UmlClassMatch(userArg: Option[UmlClass], sampleArg: Option[UmlClass], compAM: Boolean) extends Match {

  override type T = UmlClass

  override type AR = UmlClassMatchAnalysisResult

  def analyze(c1: UmlClass, c2: UmlClass): UmlClassMatchAnalysisResult = {
    val classTypeCorrect = c1.classType == c2.classType

    if (compAM) {
      val attributesResult: MatchingResult[UmlAttributeMatch] = UmlAttributeMatcher.doMatch(c1.attributes, c2.attributes)
      val methodsResult: MatchingResult[UmlMethodMatch] = UmlMethodMatcher.doMatch(c1.methods, c2.methods)

      val membersCorrect: Boolean = attributesResult.success == SuccessType.COMPLETE && methodsResult.success == SuccessType.COMPLETE

      val matchType: MatchType = (classTypeCorrect, membersCorrect) match {
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
  override protected def descArgForJson(arg: UmlClass): JsValue = Json.obj(nameName -> arg.name, classTypeName -> arg.classType.entryName)

  val attributesResult: Option[MatchingResult[UmlAttributeMatch]] = analysisResult.flatMap(_.maybeAttributeMatchingResult)
  val methodsResult   : Option[MatchingResult[UmlMethodMatch]]    = analysisResult.flatMap(_.maybeMethodMatchingResult)

}


final case class UmlClassMatcher(compareAttrsAndMethods: Boolean) extends Matcher[UmlClassMatch] {

  override type T = UmlClass

  override protected val matchName: String = "Klassen"

  override protected val matchSingularName: String = "der Klasse"

  override protected def canMatch(c1: UmlClass, c2: UmlClass): Boolean = c1.name == c2.name

  override protected def matchInstantiation(ua: Option[UmlClass], sa: Option[UmlClass]): UmlClassMatch =
    UmlClassMatch(ua, sa, compareAttrsAndMethods)

}

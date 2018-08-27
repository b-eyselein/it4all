package model.xml

import model._
import model.core.matching.{AnalysisResult, Match, MatchType, Matcher}
import model.core.result.SuccessType
import model.xml.XmlConsts._
import model.xml.dtd.ElementLine
import play.api.libs.json.{JsValue, Json}

import scala.language.postfixOps

case class ElementLineAnalysisResult(matchType: MatchType,
                                     contentCorrect: Boolean, correctContent: String,
                                     attributesCorrect: Boolean, correctAttributes: String) extends AnalysisResult {

  override def toJson: JsValue = Json.obj(
    successName -> matchType.entryName,
    "contentCorrect" -> contentCorrect, "correctContent" -> correctContent,
    "attributesCorrect" -> attributesCorrect, "correctAttributes" -> correctAttributes
  )

  def points: Points = addUp(Seq(contentCorrect, attributesCorrect).map {
    case false => 0 points
    case true  => 1 point
  })

}

case class ElementLineMatch(userArg: Option[ElementLine], sampleArg: Option[ElementLine]) extends Match[ElementLine, ElementLineAnalysisResult] with XmlEvaluationResult {

  //  override type MatchAnalysisResult = ElementLineAnalysisResult

  override protected def analyze(arg1: ElementLine, arg2: ElementLine): ElementLineAnalysisResult = {
    val contentCorrect = arg1.elementDefinition.contentAsString == arg2.elementDefinition.contentAsString

    val arg1Def = arg1.attributeLists.headOption.map(_.asString)
    val arg2Def = arg2.attributeLists.headOption.map(_.asString)
    val attributesCorrect = arg1Def == arg2Def

    val matchType = if (contentCorrect) {
      if (attributesCorrect) MatchType.SUCCESSFUL_MATCH
      else MatchType.PARTIAL_MATCH
    } else MatchType.UNSUCCESSFUL_MATCH

    ElementLineAnalysisResult(matchType,
      contentCorrect, arg2.elementDefinition.contentAsString,
      attributesCorrect, arg2Def.getOrElse("FEHLER!"))
  }

  override protected def descArgForJson(arg: ElementLine): JsValue = Json.obj(nameName -> arg.elementName)

  override def success: SuccessType = if (analysisResult.exists(_.matchType == MatchType.SUCCESSFUL_MATCH)) {
    SuccessType.COMPLETE
  } else SuccessType.NONE

}

object DocTypeDefMatcher extends Matcher[ElementLine, ElementLineAnalysisResult, ElementLineMatch] {

  override protected def canMatch: (ElementLine, ElementLine) => Boolean = _.elementName == _.elementName

  override protected def matchInstantiation: (Option[ElementLine], Option[ElementLine]) => ElementLineMatch = ElementLineMatch

}

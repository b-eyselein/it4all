package model.tools.xml

import de.uniwue.dtd.model.ElementLine
import model.core.matching.{AnalysisResult, Match, MatchType, Matcher}
import model.core.result.SuccessType
import model.points._
import model.tools.xml.XmlConsts._
import play.api.libs.json.{JsValue, Json}

import scala.language.postfixOps

final case class ElementLineAnalysisResult(matchType: MatchType,
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

final case class ElementLineMatch(userArg: Option[ElementLine], sampleArg: Option[ElementLine]) extends Match with XmlEvaluationResult {

  override type T = ElementLine

  override type AR = ElementLineAnalysisResult

  override protected def analyze(arg1: ElementLine, arg2: ElementLine): ElementLineAnalysisResult = {
    val contentCorrect = arg1.elementDefinition.contentAsString == arg2.elementDefinition.contentAsString

    val arg1Def = arg1.attributeLists.headOption.map(_.asString)
    val arg2Def = arg2.attributeLists.headOption.map(_.asString)
    val attributesCorrect = arg1Def == arg2Def

    val matchType: MatchType = if (contentCorrect) {
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

  override def points: Points = matchType match {
    case MatchType.SUCCESSFUL_MATCH                             => sampleArg map XmlGrammarCompleteResult.pointsForElementLine getOrElse (0 points)
    case MatchType.ONLY_SAMPLE                                  => 0 points
    case MatchType.ONLY_USER                                    => 0 points
    case MatchType.UNSUCCESSFUL_MATCH | MatchType.PARTIAL_MATCH =>
      // FIXME: calculate...
      analysisResult.map(_.points) getOrElse 0.points
  }

}

object DocTypeDefMatcher extends Matcher[ElementLineMatch] {

  override type T = ElementLine

  override protected val matchName: String = "DTD-Zeilen"

  override protected val matchSingularName: String = "DTD-Zeile"

  override protected def canMatch(el1: ElementLine, el2: ElementLine): Boolean = el1.elementName == el2.elementName

  override protected def matchInstantiation(ua: Option[ElementLine], sa: Option[ElementLine]): ElementLineMatch =
    ElementLineMatch(ua, sa)

}

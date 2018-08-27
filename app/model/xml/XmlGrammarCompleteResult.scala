package model.xml

import model.core.matching.{MatchType, MatchingResult}
import model.core.result.SuccessType
import model.xml.XmlConsts._
import model.xml.dtd._
import play.api.libs.json.{JsValue, Json}

import scala.language.postfixOps
import model._

case class XmlGrammarCompleteResult(learnerSolution: DTDParseResult, sampleGrammar: XmlSampleGrammar, completeEx: XmlCompleteExercise) extends XmlCompleteResult {

  override type SolType = DTDParseResult

  private val pointsForElement  : Points = 1 halfPoints
  private val pointsForAttribute: Points = 3 halfPoints

  val matchingResult: MatchingResult[ElementLine, ElementLineAnalysisResult, ElementLineMatch] = XmlCorrector.correctDTD(learnerSolution.dtd, sampleGrammar.sampleGrammar)

  private def pointsForElementLine(elementLine: ElementLine): Points = {
    val pointsForElemContent: Points = pointsForElementContent(elementLine.elementDefinition.content)
    val pointsForAttrs: Points = addUp(elementLine.attributeLists.map(pointsForAttributes))

    pointsForElemContent + pointsForAttrs
  }

  private def pointsForElementContent(elementContent: ElementContent): Points = elementContent match {
    case _: StaticElementContent        => pointsForElement
    case _: ChildElementContent         => pointsForElement
    case u: UnaryOperatorElementContent => pointsForElement + pointsForElementContent(u.childContent)
    case m: MultiElementContent         => addUp(m.children map pointsForElementContent)
  }

  private def pointsForAttributes(attributeList: AttributeList): Points = pointsForAttribute * attributeList.attributeDefinitions.size

  private def pointsForMatch(m: ElementLineMatch): Points = m.matchType match {
    case MatchType.SUCCESSFUL_MATCH                             => m.sampleArg map pointsForElementLine getOrElse (0 points)
    case MatchType.ONLY_SAMPLE                                  => 0 points
    case MatchType.ONLY_USER                                    => 0 points
    case MatchType.UNSUCCESSFUL_MATCH | MatchType.PARTIAL_MATCH =>
      // FIXME: calculate...
      m.analysisResult.map { elar: ElementLineAnalysisResult =>
        elar.points
      } getOrElse 0.points
  }

  override def maxPoints: Points = {
    val pointsForElements: Points = pointsForElement * sampleGrammar.sampleGrammar.asElementLines.size

    val pointsForContents: Points = addUp(sampleGrammar.sampleGrammar.asElementLines map pointsForElementLine)

    pointsForElements + pointsForContents
  }

  override def points: Points = success match {
    case SuccessType.COMPLETE => maxPoints
    case _                    => addUp(matchingResult.allMatches map pointsForMatch)
  }

  override val results: Seq[ElementLineMatch] = matchingResult.allMatches

  def toJson(solutionSaved: Boolean): JsValue = Json.obj(
    solutionSavedName -> solutionSaved,
    successName -> isSuccessful,
    pointsName -> points.asDoubleString,
    maxPointsName -> maxPoints.asDoubleString,
    resultsName -> results.map(_.toJson),
    parseErrorsName -> learnerSolution.parseErrors.map(e => Json.obj(messageName -> e.getMessage, parsedName -> e.parsedLine))
  )

}
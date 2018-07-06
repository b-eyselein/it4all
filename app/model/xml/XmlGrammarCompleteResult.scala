package model.xml

import model.core.matching.{MatchType, MatchingResult}
import model.core.result.SuccessType
import model.xml.XmlConsts._
import model.xml.dtd._
import play.api.libs.json.{JsValue, Json}

import scala.language.postfixOps

case class XmlGrammarCompleteResult(learnerSolution: DTDParseResult, sampleGrammar: XmlSampleGrammar, completeEx: XmlCompleteExercise) extends XmlCompleteResult {

  override type SolType = DTDParseResult

  private val pointsForElement   = 0.5
  private val pointsForAttribute = 1.5

  val matchingResult: MatchingResult[ElementLine, ElementLineAnalysisResult, ElementLineMatch] = XmlCorrector.correctDTD(learnerSolution.dtd, sampleGrammar.sampleGrammar)

  private def pointsForElementLine(elementLine: ElementLine): Double = {
    val pointsForElemContent = pointsForElementContent(elementLine.elementDefinition.content)
    val pointsForAttrs = elementLine.attributeLists.map(pointsForAttributes).sum

    pointsForElemContent + pointsForAttrs
  }

  private def pointsForElementContent(elementContent: ElementContent): Double = elementContent match {
    case _: StaticElementContent        => pointsForElement
    case _: ChildElementContent         => pointsForElement
    case u: UnaryOperatorElementContent => pointsForElement + pointsForElementContent(u.childContent)
    case m: MultiElementContent         => m.children map pointsForElementContent sum
  }

  private def pointsForAttributes(attributeList: AttributeList): Double = attributeList.attributeDefinitions.size * pointsForAttribute

  private def pointsForMatch(m: ElementLineMatch): Double = m.matchType match {
    case MatchType.SUCCESSFUL_MATCH                             => m.sampleArg.map(pointsForElementLine).getOrElse(0)
    case MatchType.ONLY_SAMPLE                                  => 0
    case MatchType.ONLY_USER                                    => 0
    case MatchType.UNSUCCESSFUL_MATCH | MatchType.PARTIAL_MATCH =>
      // FIXME: calculate...
      m.analysisResult.map { elar: ElementLineAnalysisResult =>
        elar.points
      } getOrElse 0
  }

  override def maxPoints: Double = {
    val pointsForElements = pointsForElement * sampleGrammar.sampleGrammar.asElementLines.size

    val pointsForContents = sampleGrammar.sampleGrammar.asElementLines map pointsForElementLine sum

    pointsForElements + pointsForContents
  }

  override def points: Double = success match {
    case SuccessType.COMPLETE => maxPoints
    case _                    => matchingResult.allMatches map pointsForMatch sum
  }

  override val results: Seq[ElementLineMatch] = matchingResult.allMatches

  def toJson(solutionSaved: Boolean): JsValue = Json.obj(
    solutionSavedName -> solutionSaved,
    successName -> isSuccessful,
    pointsName -> points,
    maxPointsName -> maxPoints,
    resultsName -> results.map(_.toJson),
    parseErrorsName -> learnerSolution.parseErrors.map(e => Json.obj(messageName -> e.getMessage, parsedName -> e.parsedLine))
  )

}
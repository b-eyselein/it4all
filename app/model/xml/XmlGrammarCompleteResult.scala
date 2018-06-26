package model.xml

import model.core.Levenshtein
import model.core.matching.{MatchType, MatchingResult}
import model.core.result.SuccessType
import model.xml.dtd._

import scala.language.postfixOps

case class XmlGrammarCompleteResult(learnerSolution: DocTypeDef, solutionSaved: Boolean, completeEx: XmlCompleteExercise)
  extends XmlCompleteResult {

  private val pointsForElement   = 0.5
  private val pointsForAttribute = 1.5

  override type SolType = DocTypeDef

  val grammar: XmlSampleGrammar = completeEx.sampleGrammars.minBy(sampleG => Levenshtein.levenshteinDistance(learnerSolution.asString, sampleG.sampleGrammar.asString))

  val matchingResult: MatchingResult[ElementLine, ElementLineAnalysisResult, ElementLineMatch] = XmlCorrector.correctDTD(learnerSolution, grammar.sampleGrammar)

  private def pointsForElementLine(elementLine: ElementLine): Double = {
    val pointsForElemContent = pointsForElementContent(elementLine.elementDefinition.content)
    val pointsForAttrs = elementLine.attributeLists.map(pointsForAttributes).sum

    pointsForElemContent + pointsForAttrs
  }

  private def pointsForElementContent(elementContent: ElementContent): Double = elementContent match {
    case s: StaticElementContent        => 0.5
    case c: ChildElementContent         => 0.5
    case u: UnaryOperatorElementContent => 0.5 + pointsForElementContent(u.childContent)
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
    val pointsForElements = pointsForElement * grammar.sampleGrammar.asElementLines.size

    val pointsForContents = grammar.sampleGrammar.asElementLines map pointsForElementLine sum

    pointsForElements + pointsForContents
  }

  override def points: Double = success match {
    case SuccessType.COMPLETE => maxPoints
    case _                    => matchingResult.allMatches map pointsForMatch sum
  }

  override val results: Seq[ElementLineMatch] = matchingResult.allMatches

}
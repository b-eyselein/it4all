package model.xml

import de.uniwue.dtd.model._
import de.uniwue.dtd.parser._
import model._
import model.core.matching.MatchingResult
import model.core.result.SuccessType
import model.xml.XmlGrammarCompleteResult._

import scala.language.postfixOps

object XmlGrammarCompleteResult {

  private val pointsForElement  : Points = 1 halfPoints
  private val pointsForAttribute: Points = 3 halfPoints

  def pointsForElementLine(elementLine: ElementLine): Points = {
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

}

final case class XmlGrammarCompleteResult(learnerSolution: DTDParseResult, sample: XmlSample, completeEx: XmlCompleteEx) extends XmlCompleteResult {

  override type SolType = DTDParseResult

  val matchingResult: MatchingResult[ElementLineMatch] = XmlCorrector.correctDTD(learnerSolution.dtd, sample.sampleGrammar)

  override def maxPoints: Points = {
    val pointsForElements: Points = pointsForElement * sample.sampleGrammar.asElementLines.size

    val pointsForContents: Points = addUp(sample.sampleGrammar.asElementLines map pointsForElementLine)

    pointsForElements + pointsForContents
  }

  override def points: Points = success match {
    case SuccessType.COMPLETE => maxPoints
    case _                    => addUp(matchingResult.allMatches map (_.points))
  }

  override val results: Seq[ElementLineMatch] = matchingResult.allMatches

}
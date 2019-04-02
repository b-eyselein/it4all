package model.tools.xml

import de.uniwue.dtd.model._
import de.uniwue.dtd.parser._
import model.points._
import model.core.matching.MatchingResult
import model.core.result.SuccessType
import model.tools.xml.XmlGrammarCompleteResult._

import scala.language.postfixOps
import scala.util.Success

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

final case class XmlGrammarCompleteResult(learnerSolution: DTDParseResult, sample: XmlSampleSolution, exercise: XmlExercise) extends XmlCompleteResult {

  override type SolType = DTDParseResult

  private val sampleGrammar = DocTypeDefParser.tryParseDTD(sample.sample.grammar) match {
    case Success(dtd) => dtd
    case _            => ???
  }

  val matchingResult: MatchingResult[ElementLineMatch] = XmlCorrector.correctDTD(learnerSolution.dtd, sampleGrammar)

  override def maxPoints: Points = {
    val pointsForElements: Points = pointsForElement * sampleGrammar.asElementLines.size

    val pointsForContents: Points = addUp(sampleGrammar.asElementLines map pointsForElementLine)

    pointsForElements + pointsForContents
  }

  override def points: Points = success match {
    case SuccessType.COMPLETE => maxPoints
    case _                    => addUp(matchingResult.allMatches map (_.points))
  }

  override val results: Seq[ElementLineMatch] = matchingResult.allMatches

}
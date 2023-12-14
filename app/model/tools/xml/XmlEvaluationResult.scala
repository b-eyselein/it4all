package model.tools.xml

import de.uniwue.dtd.model._
import enumeratum.{Enum, EnumEntry}
import model.matching.{Match, MatchType}
import model.points._
import org.xml.sax.SAXParseException

sealed trait XmlEvaluationResult

// Document

sealed abstract class XmlErrorType(val german: String) extends EnumEntry

object XmlErrorType extends Enum[XmlErrorType] {

  case object FATAL   extends XmlErrorType("Fataler Fehler")
  case object ERROR   extends XmlErrorType("Fehler")
  case object WARNING extends XmlErrorType("Warnung")

  val values: IndexedSeq[XmlErrorType] = findValues

}

object XmlError {
  def fromSAXParseException(errorType: XmlErrorType, e: SAXParseException): XmlError = XmlError(errorType, e.getMessage, e.getLineNumber)
}

final case class XmlError(
  errorType: XmlErrorType,
  errorMessage: String,
  line: Int
) extends XmlEvaluationResult

// Grammar

object ElementLineMatch {

  val pointsForElement: Points   = 1.halfPoints
  val pointsForAttribute: Points = 3.halfPoints

}

final case class ElementLineMatch(
  matchType: MatchType,
  userArg: ElementLine,
  sampleArg: ElementLine,
  analysisResult: ElementLineAnalysisResult
) extends Match[ElementLine]
    with XmlEvaluationResult {

  import ElementLineMatch._

  override def maxPoints: Points = pointsForElement + pointsForElementLine(sampleArg)

  override def points: Points = matchType match {
    case MatchType.SUCCESSFUL_MATCH                             => maxPoints
    case MatchType.UNSUCCESSFUL_MATCH | MatchType.PARTIAL_MATCH =>
      // FIXME: calculate...

      val pointsForContent = {
        val pointsForElemContent = if (analysisResult.contentCorrect) singlePoint else zeroPoints
        val pointsForAttributes  = if (analysisResult.attributesCorrect) singlePoint else zeroPoints
        pointsForElemContent + pointsForAttributes
      }

      pointsForElement + pointsForContent
  }

  private def pointsForElementLine(elementLine: ElementLine): Points = {
    val pointsForElemContent: Points = pointsForElementContent(elementLine.elementDefinition.content)
    val pointsForAttrs: Points       = addUp(elementLine.attributeLists.map(pointsForAttributes))

    pointsForElemContent + pointsForAttrs
  }

  private def pointsForElementContent(elementContent: ElementContent): Points =
    elementContent match {
      case _: StaticElementContent        => pointsForElement
      case _: ChildElementContent         => pointsForElement
      case u: UnaryOperatorElementContent => pointsForElement + pointsForElementContent(u.childContent)
      case m: MultiElementContent         => addUp(m.children.map(pointsForElementContent))
    }

  private def pointsForAttributes(attributeList: AttributeList): Points =
    pointsForAttribute * attributeList.attributeDefinitions.size
  //  val maxPoints = {
  //    val pointsForElements: Points = XmlGrammarCompleteResult.pointsForElement * sampleGrammar.asElementLines.size
  //
  //    val pointsForContents: Points = addUp(sampleGrammar.asElementLines.map(XmlGrammarCompleteResult.pointsForElementLine))
  //
  //    pointsForElements + pointsForContents
  //  }

}

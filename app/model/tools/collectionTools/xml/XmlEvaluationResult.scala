package model.tools.collectionTools.xml

import de.uniwue.dtd.model._
import enumeratum.{EnumEntry, PlayEnum}
import model.core.matching.{Match, MatchType}
import model.core.result.SuccessType
import model.points.{Points, addUp, singlePoint, zeroPoints, _}
import org.xml.sax.SAXParseException

import scala.collection.immutable.IndexedSeq

sealed trait XmlEvaluationResult

// Document

sealed abstract class XmlErrorType(val german: String) extends EnumEntry

object XmlErrorType extends PlayEnum[XmlErrorType] {

  val values: IndexedSeq[XmlErrorType] = findValues

  case object FATAL extends XmlErrorType("Fataler Fehler")

  case object ERROR extends XmlErrorType("Fehler")

  case object WARNING extends XmlErrorType("Warnung")

}

object XmlError {

  def fromSAXParseException(errorType: XmlErrorType, e: SAXParseException): XmlError =
    XmlError(errorType, e.getMessage, e.getLineNumber, errorType match {
      case XmlErrorType.WARNING => SuccessType.PARTIALLY
      case _                    => SuccessType.NONE
    })

}

final case class XmlError(
  errorType: XmlErrorType,
  errorMessage: String,
  line: Int,
  success: SuccessType
) extends XmlEvaluationResult

// Grammar

object ElementLineMatch {

  val pointsForElement: Points   = 1.halfPoints
  val pointsForAttribute: Points = 3.halfPoints

}

final case class ElementLineMatch(
  matchType: MatchType,
  userArg: Option[ElementLine],
  sampleArg: Option[ElementLine],
  maybeAnalysisResult: Option[ElementLineAnalysisResult]
) extends Match[ElementLine]
    with XmlEvaluationResult {

  import ElementLineMatch._

  //  override protected def descArgForJson(arg: ElementLine): JsValue = Json.obj(nameName -> arg.elementName)

  // override
  def success: SuccessType =
    if (matchType == MatchType.SUCCESSFUL_MATCH) {
      SuccessType.COMPLETE
    } else {
      SuccessType.NONE
    }

  override def maxPoints: Points = sampleArg match {
    case None     => zeroPoints
    case Some(sa) => pointsForElement + pointsForElementLine(sa)
  }

  override def points: Points = matchType match {
    case MatchType.SUCCESSFUL_MATCH                             => maxPoints
    case MatchType.ONLY_SAMPLE | MatchType.ONLY_USER            => zeroPoints
    case MatchType.UNSUCCESSFUL_MATCH | MatchType.PARTIAL_MATCH =>
      // FIXME: calculate...

      val pointsForContent = maybeAnalysisResult match {
        case None => zeroPoints
        case Some(ar) =>
          val pointsForElemContent = if (ar.contentCorrect) singlePoint else zeroPoints
          val pointsForAttributes  = if (ar.attributesCorrect) singlePoint else zeroPoints
          pointsForElemContent + pointsForAttributes
      }

      pointsForElement + pointsForContent
  }

  private def pointsForElementLine(elementLine: ElementLine): Points = {
    val pointsForElemContent: Points = pointsForElementContent(elementLine.elementDefinition.content)
    val pointsForAttrs: Points       = addUp(elementLine.attributeLists.map(pointsForAttributes))

    pointsForElemContent + pointsForAttrs
  }

  private def pointsForElementContent(elementContent: ElementContent): Points = elementContent match {
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

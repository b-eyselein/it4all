package model.tools.xml

import de.uniwue.dtd.parser.DTDParseException
import enumeratum.{EnumEntry, PlayEnum}
import model.core.result.{CompleteResult, EvaluationResult, SuccessType}
import model.points._
import org.xml.sax.SAXParseException

import scala.collection.immutable.IndexedSeq


trait XmlEvaluationResult extends EvaluationResult

trait XmlCompleteResult extends CompleteResult[XmlEvaluationResult] {

  def solutionSaved: Boolean

}

// Document result

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


final case class XmlError(errorType: XmlErrorType, errorMessage: String, line: Int, success: SuccessType)
  extends XmlEvaluationResult


final case class XmlDocumentCompleteResult(
  successType: SuccessType,
  results: Seq[XmlError],
  points: Points = (-1).points,
  maxPoints: Points = (-1).points,
  solutionSaved: Boolean = false
) extends XmlCompleteResult


// Grammar result

object XmlGrammarCompleteResult {

  val pointsForElement  : Points = 1.halfPoints
  val pointsForAttribute: Points = 3.halfPoints

}

final case class XmlGrammarCompleteResult(
  successType: SuccessType,
  parseErrors: Seq[DTDParseException] = Seq.empty,
  results: Seq[ElementLineMatch],
  points: Points,
  maxPoints: Points,
  solutionSaved: Boolean = false
) extends XmlCompleteResult

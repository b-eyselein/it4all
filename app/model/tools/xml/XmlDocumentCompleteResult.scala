package model.tools.xml

import enumeratum.{EnumEntry, PlayEnum}
import model.core.result.SuccessType
import model.points._
import org.xml.sax.SAXParseException

import scala.collection.immutable.IndexedSeq

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


final case class XmlDocumentCompleteResult(learnerSolution: String, results: Seq[XmlError]) extends XmlCompleteResult {

  override type SolType = String

  override def isSuccessful: Boolean = results.isEmpty

  override def points: Points = (-1).points

  override def maxPoints: Points = (-1).points

}

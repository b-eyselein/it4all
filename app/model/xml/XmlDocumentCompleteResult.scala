package model.xml

import enumeratum.{EnumEntry, PlayEnum}
import model.core.result.SuccessType
import org.xml.sax.SAXParseException

import scala.collection.immutable.IndexedSeq

sealed abstract class XmlErrorType(val german: String) extends EnumEntry

object XmlErrorType extends PlayEnum[XmlErrorType] {

  val values: IndexedSeq[XmlErrorType] = findValues

  case object FATAL extends XmlErrorType("Fataler Fehler")

  case object ERROR extends XmlErrorType("Fehler")

  case object WARNING extends XmlErrorType("Warnung")

}


class XmlError(val errorType: XmlErrorType, e: SAXParseException) extends XmlEvaluationResult {

  def errorMessage: String = e.getMessage

  def line: Int = e.getLineNumber

  override val success: SuccessType = errorType match {
    case XmlErrorType.WARNING => SuccessType.PARTIALLY
    case _                    => SuccessType.NONE
  }

}


final case class XmlDocumentCompleteResult(learnerSolution: String, results: Seq[XmlError]) extends XmlCompleteResult {

  override type SolType = String

  override def isSuccessful: Boolean = results.isEmpty

}
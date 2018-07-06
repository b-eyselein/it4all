package model.xml

import enumeratum.{Enum, EnumEntry}
import model.core.result.SuccessType
import model.xml.XmlConsts._
import org.xml.sax.SAXParseException
import play.api.libs.json.{JsObject, JsValue, Json}

import scala.collection.immutable.IndexedSeq

sealed abstract class XmlErrorType(val german: String) extends EnumEntry

object XmlErrorType extends Enum[XmlErrorType] {

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

  val lineStr: String = if (line != -1) s" in Zeile $line" else ""

  override def toJson: JsObject = Json.obj("errorType" -> errorType.entryName, "errorMessage" -> errorMessage, "line" -> line, "success" -> success.entryName)

}


case class XmlDocumentCompleteResult(learnerSolution: String, results: Seq[XmlError]) extends XmlCompleteResult {

  override type SolType = String

  def toJson(solutionSaved: Boolean): JsValue = Json.obj(
    solutionSavedName -> solutionSaved,
    successName -> isSuccessful,
    pointsName -> points,
    maxPointsName -> maxPoints,
    resultsName -> results.map(_.toJson)
  )

}
package model.tools.xml

import de.uniwue.dtd.parser.DTDParseException
import model.core.result.CompleteResultJsonProtocol
import play.api.libs.json._
import play.api.libs.functional.syntax._
import model.tools.xml.XmlConsts._

object XmlSampleSolutionJsonProtocol {

  private implicit val xmlSolutionFormat: Format[XmlSolution] = Json.format[XmlSolution]

  val xmlSampleSolutionJsonFormat: Format[XmlSampleSolution] = Json.format[XmlSampleSolution]

}

object XmlCompleteResultJsonProtocol extends CompleteResultJsonProtocol[XmlEvaluationResult, XmlCompleteResult] {

  override def completeResultWrites(solutionSaved: Boolean): Writes[XmlCompleteResult] = {
    case xmlGrammarCompleteResult: XmlGrammarCompleteResult   => xmlGrammarCompleteResultWrites(solutionSaved).writes(xmlGrammarCompleteResult)
    case xmlDocumentCompleteResult: XmlDocumentCompleteResult => xmlDocumentCompleteResultWrites(solutionSaved).writes(xmlDocumentCompleteResult)
  }

  // Xml Grammar correction

  private def xmlGrammarCompleteResultWrites(solutionSaved: Boolean): Writes[XmlGrammarCompleteResult] = (
    (__ \ solutionSavedName).write[Boolean] and
      (__ \ successName).write[Boolean] and
      (__ \ pointsName).write[String] and
      (__ \ maxPointsName).write[String] and
      (__ \ resultsName).write[Seq[ElementLineMatch]] and
      (__ \ parseErrorsName).write[Seq[DTDParseException]]
    ) (xmlGramCompRes =>
    (solutionSaved, xmlGramCompRes.isSuccessful, xmlGramCompRes.points.asDoubleString, xmlGramCompRes.maxPoints.asDoubleString,
      xmlGramCompRes.results, xmlGramCompRes.learnerSolution.parseErrors)
  )

  private implicit val elementLineMatchWrites: Writes[ElementLineMatch] = _.toJson

  private implicit val dtdParseExceptionWrites: Format[DTDParseException] = Json.format[DTDParseException]

  // Xml Document correction

  private implicit val errorTypeJsonFormat: Format[XmlErrorType] = XmlErrorType.jsonFormat

  private def xmlDocumentCompleteResultWrites(solSaved: Boolean): Writes[XmlDocumentCompleteResult] = (
    (__ \ solutionSavedName).write[Boolean] and
      (__ \ successName).write[Boolean] and
      (__ \ pointsName).write[String] and
      (__ \ maxPointsName).write[String] and
      (__ \ resultsName).write[Seq[XmlError]]
    ) (unapplyXmlDocumentCompleteResult(solSaved))

  private def unapplyXmlDocumentCompleteResult(solSaved: Boolean):
  XmlDocumentCompleteResult => (Boolean, Boolean, String, String, Seq[XmlError]) = xmlDocCompRes =>
    (solSaved, xmlDocCompRes.isSuccessful, xmlDocCompRes.points.asDoubleString, xmlDocCompRes.maxPoints.asDoubleString, xmlDocCompRes.results)


  private implicit val xmlErrorWrites: Writes[XmlError] = Json.writes[XmlError]

}

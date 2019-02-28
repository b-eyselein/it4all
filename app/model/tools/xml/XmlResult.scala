package model.tools.xml

import de.uniwue.dtd.parser.DTDParseException
import model.core.result.{CompleteResult, CompleteResultJsonProtocol, EvaluationResult}
import model.tools.xml.XmlConsts._
import play.api.libs.functional.syntax._
import play.api.libs.json.{Writes, _}

trait XmlEvaluationResult extends EvaluationResult

trait XmlCompleteResult extends CompleteResult[XmlEvaluationResult]


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

  private implicit val dtdParseExceptionWrites: Writes[DTDParseException] = (
    (__ \ messageName).write[String] and
      (__ \ parsedName).write[String]
    ) (e => (e.getMessage, e.parsedLine))

  // Xml Document correction

  private def xmlDocumentCompleteResultWrites(solSaved: Boolean): Writes[XmlDocumentCompleteResult] = (
    (__ \ solutionSavedName).write[Boolean] and
      (__ \ successName).write[Boolean] and
      (__ \ pointsName).write[String] and
      (__ \ maxPointsName).write[String] and
      (__ \ resultsName).write[Seq[XmlError]]
    ) (xmlDocCompRes => {
    (solSaved, xmlDocCompRes.isSuccessful, xmlDocCompRes.points.asDoubleString, xmlDocCompRes.maxPoints.asDoubleString, xmlDocCompRes.results)
  })

  private implicit val xmlErrorWrites: Writes[XmlError] = (
    (__ \ "errorType").write[String] and
      (__ \ "errorMessage").write[String] and
      (__ \ lineName).write[Int] and
      (__ \ successName).write[String]
    ) (xe => (xe.errorType.entryName, xe.errorMessage, xe.line, xe.success.entryName))

}

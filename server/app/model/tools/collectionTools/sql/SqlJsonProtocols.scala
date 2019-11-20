package model.tools.collectionTools.sql

import model.core.{LongText, LongTextJsonProtocol}
import model.tools.ToolJsonProtocol
import model.tools.collectionTools.sql.SqlConsts._
import model.{SemanticVersion, SemanticVersionHelper, StringSampleSolution, StringSampleSolutionJsonProtocol}
import play.api.libs.functional.syntax._
import play.api.libs.json._

object SqlJsonProtocols extends ToolJsonProtocol[SqlExercise, StringSampleSolution, SqlCorrResult] {

  override val sampleSolutionFormat: Format[StringSampleSolution] = StringSampleSolutionJsonProtocol.stringSampleSolutionJsonFormat

  override val exerciseFormat: Format[SqlExercise] = {
    implicit val svf : Format[SemanticVersion]      = SemanticVersionHelper.format
    implicit val ltf : Format[LongText]             = LongTextJsonProtocol.format
    implicit val sssf: Format[StringSampleSolution] = sampleSolutionFormat

    Json.format[SqlExercise]
  }

  // Other

  private val sqlQueryCellWrites: Writes[SqlCell] = (
    (__ \ "content").write[String] and
      (__ \ "different").write[Boolean]
    ) (sc => (sc.content, sc.different))

  private implicit val sqlQueryRowWrites: Writes[SqlRow] = row => {
    JsObject(row.cells.map {
      case (colName, sqlCell) => colName -> sqlQueryCellWrites.writes(sqlCell)
    })
  }

  private val sqlQueryResultWrites: Writes[SqlQueryResult] = (
    (__ \ "colNames").write[Seq[String]] and //-> columnNames,
      (__ \ "content").write[Seq[SqlRow]]
    ) (sqr => (sqr.columnNames, sqr.rows))

  private val sqlExecutionResultWrites: Writes[SqlExecutionResult] = {
    implicit val sqrw: Writes[SqlQueryResult] = sqlQueryResultWrites

    (
      (__ \ successName).write[String] and // -> success,
        (__ \ userResultName).write[Option[SqlQueryResult]] and //  match {
        //      case Success(result) => userResultName -> result.toJson
        //      case Failure(error)  => userErrorName -> JsString(error.toString)
        //    },
        (__ \ sampleResultName).write[Option[SqlQueryResult]]
      ) (ser => (ser.success.entryName, ser.userResultTry.toOption, ser.sampleResultTry.toOption))
  }

  private val sqlResultRestWrites: Writes[SqlResult] = {
    implicit val serw: Writes[SqlExecutionResult] = sqlExecutionResultWrites

    (
      (__ \ columnComparisonsName).write[JsValue] and
        (__ \ tableComparisonsName).write[JsValue] and
        (__ \ joinExpressionsComparisonsName).write[JsValue] and
        (__ \ whereComparisonsName).write[JsValue] and
        (__ \ additionalComparisonsName).write[Seq[JsValue]] and
        (__ \ executionResultsName).write[SqlExecutionResult]
      ) (unapplySqlResultRest)
  }

  private def unapplySqlResultRest: SqlResult => (JsObject, JsObject, JsObject, JsObject, Seq[JsObject], SqlExecutionResult) = {
    sr: SqlResult =>
      (sr.columnComparison.toJson, sr.tableComparison.toJson, sr.joinExpressionComparison.toJson, sr.whereComparison.toJson,
        sr.additionalComparisons.map(_.toJson), sr.executionResult)
  }

  private val sqlCorrResultRestWrites: Writes[SqlCorrResult] = {
    case SqlParseFailed(error, _, _) => Json.obj(messageName -> error.getMessage)
    case sr: SqlResult               => sqlResultRestWrites.writes(sr)
  }

  override val completeResultWrites: Writes[SqlCorrResult] = (
    (__ \ solutionSavedName).write[Boolean] and
      (__ \ successName).write[String] and
      (__ \ pointsName).write[Double] and
      (__ \ maxPointsName).write[Double] and
      (__ \ resultsName).write[SqlCorrResult](sqlCorrResultRestWrites)
    ) (scr => (scr.solutionSaved, scr.successType.entryName, scr.points.asDouble, scr.maxPoints.asDouble, scr))

}

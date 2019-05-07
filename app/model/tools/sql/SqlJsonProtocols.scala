package model.tools.sql

import model.core.result.{CompleteResultJsonProtocol, EvaluationResult}
import model.tools.sql.SqlConsts._
import play.api.libs.functional.syntax._
import play.api.libs.json._

object SqlJsonProtocols extends CompleteResultJsonProtocol[EvaluationResult, SqlCorrResult] {

  private val sqlQueryCellWrites: Writes[SqlCell] = (
    (__ \ "content").write[String] and
      (__ \ "different").write[Boolean]
    ) (sc => (sc.content, sc.different))

  private implicit val sqlQueryRowWrites: Writes[SqlRow] = row => {
    JsObject(row.cells.map {
      case (colName, sqlCell) => colName -> sqlQueryCellWrites.writes(sqlCell)
    })
  }

  private implicit val sqlQueryResultWrites: Writes[SqlQueryResult] = (
    (__ \ "colNames").write[Seq[String]] and //-> columnNames,
      (__ \ "content").write[Seq[SqlRow]] // -> JsArray(rows.map(_.toJson))
    ) (sqr => (sqr.columnNames, sqr.rows))

  private implicit val sqlExecutionResultWrites: Writes[SqlExecutionResult] = (
    (__ \ successName).write[String] and // -> success,
      (__ \ userResultName).write[Option[SqlQueryResult]] and //  match {
      //      case Success(result) => userResultName -> result.toJson
      //      case Failure(error)  => userErrorName -> JsString(error.toString)
      //    },
      (__ \ sampleResultName).write[Option[SqlQueryResult]] // -> sampleResultTry.map(_.toJson).toOption
    ) (ser => (ser.success.entryName, ser.userResultTry.toOption, ser.sampleResultTry.toOption))

  private val sqlResultRestWrites: Writes[SqlResult] = (
    (__ \ columnComparisonsName).write[JsValue] and
      (__ \ tableComparisonsName).write[JsValue] and
      (__ \ joinExpressionsComparisonsName).write[JsValue] and
      (__ \ whereComparisonsName).write[JsValue] and
      (__ \ additionalComparisonsName).write[Seq[JsValue]] and
      (__ \ executionResultsName).write[SqlExecutionResult]
    ) (unapplySqlResultRest)

  private def unapplySqlResultRest: SqlResult => (JsObject, JsObject, JsObject, JsObject, Seq[JsObject], SqlExecutionResult) = {
    sr: SqlResult =>
      (sr.columnComparison.toJson, sr.tableComparison.toJson, sr.joinExpressionComparison.toJson, sr.whereComparison.toJson,
        sr.additionalComparisons.map(_.toJson), sr.executionResult)
  }

  private implicit val sqlCorrResultRestWrites: Writes[SqlCorrResult] = {
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

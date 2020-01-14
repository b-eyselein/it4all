package model.tools.collectionTools.sql

import model.core.matching.GenericAnalysisResult
import model.tools.collectionTools.sql.SqlConsts._
import model.tools.collectionTools.sql.SqlToolMain._
import model.tools.collectionTools.sql.matcher._
import model.tools.collectionTools.{SampleSolution, StringSampleSolutionToolJsonProtocol, ToolJsonProtocol}
import net.sf.jsqlparser.expression.operators.relational.ExpressionList
import net.sf.jsqlparser.expression.{BinaryExpression, Expression}
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.select.{Limit, OrderByElement}
import play.api.libs.functional.syntax._
import play.api.libs.json._

object SqlJsonProtocols extends StringSampleSolutionToolJsonProtocol[SqlExerciseContent, SqlCorrResult] {

  override val exerciseContentFormat: Format[SqlExerciseContent] = {
    implicit val sssf: Format[SampleSolution[String]] = sampleSolutionFormat

    Json.format[SqlExerciseContent]
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
      ) (ser => (ser.success.entryName, ser.userResultTry, ser.sampleResultTry))
  }

  private val columnMatchingResultWrites: Writes[ColumnComparison] = matchingResultWrites({
    implicit val columnWrapperWrites: Writes[ColumnWrapper]         = cw => JsString(cw.getColName)
    implicit val garw               : Writes[GenericAnalysisResult] = ToolJsonProtocol.genericAnalysisResultWrites

    Json.writes[ColumnMatch]
  })

  private val tableMatchingResultWrites: Writes[TableComparison] = matchingResultWrites({
    implicit val tr  : Writes[Table]                 = t => JsString(t.getName)
    implicit val garw: Writes[GenericAnalysisResult] = ToolJsonProtocol.genericAnalysisResultWrites

    Json.writes[TableMatch]
  })

  private val binaryExpressionResultWrites: Writes[BinaryExpressionComparison] = matchingResultWrites({
    implicit val binaryExpressionWrites: Writes[BinaryExpression]      = be => JsString(be.toString)
    implicit val garw                  : Writes[GenericAnalysisResult] = ToolJsonProtocol.genericAnalysisResultWrites

    Json.writes[BinaryExpressionMatch]
  })

  private val selectAdditionalComparisonsWrites: Writes[SelectAdditionalComparisons] = {
    implicit val groupByComparisonWrites: Writes[GroupByComparison] = matchingResultWrites({
      implicit val ew  : Writes[Expression]            = e => JsString(e.toString)
      implicit val garw: Writes[GenericAnalysisResult] = ToolJsonProtocol.genericAnalysisResultWrites

      Json.writes[GroupByMatch]
    })

    implicit val orderByComparisonWrites: Writes[OrderByComparison] = matchingResultWrites({
      implicit val obew: Writes[OrderByElement]        = obe => JsString(obe.toString)
      implicit val garw: Writes[GenericAnalysisResult] = ToolJsonProtocol.genericAnalysisResultWrites

      Json.writes[OrderByMatch]
    })

    implicit val limitComparisonWrites: Writes[LimitComparison] = matchingResultWrites({
      implicit val lew : Writes[Limit]                 = l => JsString(l.toString)
      implicit val garw: Writes[GenericAnalysisResult] = ToolJsonProtocol.genericAnalysisResultWrites

      Json.writes[LimitMatch]
    })

    Json.writes
  }

  private val additionalComparisonWrites: Writes[AdditionalComparison] = {
    implicit val sacw: Writes[SelectAdditionalComparisons] = selectAdditionalComparisonsWrites

    implicit val ivcw: Writes[InsertComparison] = matchingResultWrites({
      implicit val elw : Writes[ExpressionList]        = el => JsString(el.toString)
      implicit val garw: Writes[GenericAnalysisResult] = ToolJsonProtocol.genericAnalysisResultWrites

      Json.writes[ExpressionListMatch]
    })

    Json.writes
  }

  private val sqlResultRestWrites: Writes[SqlResult] = {
    implicit val cmrw: Writes[ColumnComparison] = columnMatchingResultWrites

    implicit val tmrw: Writes[TableComparison] = tableMatchingResultWrites

    implicit val berw: Writes[BinaryExpressionComparison] = binaryExpressionResultWrites

    implicit val acw: Writes[AdditionalComparison] = additionalComparisonWrites

    implicit val erw: Writes[SqlExecutionResult] = sqlExecutionResultWrites

    Json.writes
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

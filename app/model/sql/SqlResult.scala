package model.sql

import model.Points
import model.core.matching.MatchingResult
import model.core.result.{CompleteResult, EvaluationResult, SuccessType}
import model.sql.SqlConsts._
import model.sql.matcher._
import net.sf.jsqlparser.expression.operators.relational.ExpressionList
import net.sf.jsqlparser.expression.{BinaryExpression, Expression}
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.select.OrderByElement
import play.api.libs.json._

import scala.util.{Failure, Success, Try}

case class WrongStatementTypeException(awaited: String, gotten: String) extends Exception(s"Wrong type of statement! Expected '$awaited', bot got '$gotten'")

class SqlStatementException(cause: Throwable) extends Exception(cause) {

  override def getMessage: String = {

    @annotation.tailrec
    def go(cause: Throwable): String = {
      if (Option(cause.getMessage).isDefined) cause.getMessage
      else if (Option(cause.getCause).isEmpty) ""
      else go(cause.getCause)
    }

    go(cause)
  }

}

abstract class SqlCorrResult extends CompleteResult[EvaluationResult] {

  override type SolType = String

  val successType: SuccessType

  override def toJson(solutionSaved: Boolean): JsValue = Json.obj(
    solutionSavedName -> solutionSaved,
    successName -> successType.entryName,
    resultsName -> jsonRest
  )

  def jsonRest: JsValue

}

// FIXME: use builder?
case class SqlResult(learnerSolution: String, override val points: Points, override val maxPoints: Points,
                     columnComparison: MatchingResult[ColumnWrapper, ColumnMatch],
                     tableComparison: MatchingResult[Table, TableMatch],
                     whereComparison: MatchingResult[BinaryExpression, BinaryExpressionMatch],

                     executionResult: SqlExecutionResult,

                     groupByComparison: Option[MatchingResult[Expression, GroupByMatch]],
                     orderByComparison: Option[MatchingResult[OrderByElement, OrderByMatch]],
                     insertedValuesComparison: Option[MatchingResult[ExpressionList, ExpressionListMatch]])
  extends SqlCorrResult {

  override def results: Seq[EvaluationResult] = Seq(columnComparison, tableComparison, whereComparison, executionResult) ++ groupByComparison ++ orderByComparison

  override val successType: SuccessType = if (EvaluationResult.allResultsSuccessful(results)) SuccessType.COMPLETE else SuccessType.PARTIALLY

  override def jsonRest: JsValue = Json.obj(
    columnsName -> columnComparison.toJson,
    tablesName -> tableComparison.toJson,
    "wheres" -> whereComparison.toJson,

    "groupBy" -> groupByComparison.map(_.toJson),
    "orderBy" -> orderByComparison.map(_.toJson),
    "insertedValues" -> insertedValuesComparison.map(_.toJson),

    executionName -> executionResult.toJson
  )

}

case class SqlParseFailed(learnerSolution: String, error: Throwable) extends SqlCorrResult {

  override def results: Seq[EvaluationResult] = Seq.empty

  override val successType: SuccessType = SuccessType.ERROR

  override def jsonRest: JsValue = Json.obj(messageName -> error.getMessage)

}

case class SqlExecutionResult(userResultTry: Try[SqlQueryResult], sampleResultTry: Try[SqlQueryResult]) extends EvaluationResult {

  override val success: SuccessType = (userResultTry, sampleResultTry) match {
    case (Success(userResult), Success(sampleResult)) => SuccessType.ofBool(userResult isIdentic sampleResult)

    case (Failure(_), Success(_)) => SuccessType.ERROR

    case (Success(_), Failure(_)) => SuccessType.PARTIALLY

    case (Failure(_), Failure(_)) => SuccessType.ERROR
  }


  def toJson: JsObject = {
    val userTable: (String, JsValue) = userResultTry match {
      case Success(result) => userResultName -> result.toJson
      case Failure(error)  => userErrorName -> JsString(error.toString)
    }
    val sampleTable: JsValue = sampleResultTry.map(_.toJson) getOrElse JsNull

    JsObject

    JsObject(Seq(userTable, sampleResultName -> sampleTable))
  }

}

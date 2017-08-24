package model.querycorrectors;

import model.conditioncorrector.ExpressionMatch;
import model.exercise.Success;
import model.matching.ScalaMatch;
import model.matching.ScalaMatchingResult;
import model.result.EvaluationResult;
import net.sf.jsqlparser.expression.Expression;
import model.sql.SqlQueryResult
import net.sf.jsqlparser.statement.select.OrderByElement

case class SqlResult(learnerSolution: String,
  columnComparison: ScalaMatchingResult[ColumnWrapper, ColumnMatch],
  tableComparison: ScalaMatchingResult[String, ScalaMatch[String]],
  whereComparison: ScalaMatchingResult[Expression, ExpressionMatch],

  executionResult: SqlExecutionResult,

  groupByComparison: Option[ScalaMatchingResult[Expression, GroupByMatch]],
  orderByComparison: Option[ScalaMatchingResult[OrderByElement, OrderByMatch]])
    extends EvaluationResult(Success.NONE) {

  def getMatchingResults: List[ScalaMatchingResult[_, _ <: ScalaMatch[_]]] =
    // Option only gets added if isDefined
    List(columnComparison, tableComparison, whereComparison) ++ groupByComparison ++ orderByComparison

  def getResults = List(columnComparison, tableComparison, whereComparison, executionResult) ++ groupByComparison ++ orderByComparison
}

case class SqlExecutionResult(userResult: SqlQueryResult, sampleResult: SqlQueryResult) extends EvaluationResult(SqlExecutionResult.analyze(userResult, sampleResult))

object SqlExecutionResult {
  def analyze(userResult: SqlQueryResult, sampleResult: SqlQueryResult) =
    if (userResult == null || sampleResult == null)
      Success.FAILURE
    else if (!userResult.isIdentic(sampleResult)) Success.NONE else Success.COMPLETE
}
package model.querycorrectors;

import model.conditioncorrector.BinaryExpressionMatch;
import model.exercise.Success;
import model.matching.ScalaMatch;
import model.matching.ScalaMatchingResult;
import model.result.EvaluationResult;
import net.sf.jsqlparser.expression.Expression;
import model.sql.SqlQueryResult
import net.sf.jsqlparser.statement.select.OrderByElement
import net.sf.jsqlparser.expression.BinaryExpression

case class SqlResult(learnerSolution: String,
  columnComparison: ScalaMatchingResult[ColumnWrapper, ColumnMatch],
  tableComparison: ScalaMatchingResult[String, ScalaMatch[String]],
  whereComparison: ScalaMatchingResult[BinaryExpression, BinaryExpressionMatch],

  executionResult: SqlExecutionResult,

  groupByComparison: Option[ScalaMatchingResult[Expression, GroupByMatch]],
  orderByComparison: Option[ScalaMatchingResult[OrderByElement, OrderByMatch]])
    extends EvaluationResult(Success.NONE) {

  def getMatchingResults: List[ScalaMatchingResult[_, _ <: ScalaMatch[_]]] =
    List(columnComparison, tableComparison, whereComparison) ++ groupByComparison ++ orderByComparison

  def notEmptyMatchingResults = getMatchingResults.filter(!_.allMatches.isEmpty)

  def getResults = List(columnComparison, tableComparison, whereComparison, executionResult) ++ groupByComparison ++ orderByComparison
}

case class SqlExecutionResult(userResult: SqlQueryResult, sampleResult: SqlQueryResult) extends EvaluationResult(SqlExecutionResult.analyze(userResult, sampleResult))

object SqlExecutionResult {
  def analyze(userResult: SqlQueryResult, sampleResult: SqlQueryResult) =
    if (userResult == null || sampleResult == null)
      Success.FAILURE
    else if (!userResult.isIdentic(sampleResult)) Success.NONE else Success.COMPLETE
}
package model.querycorrectors;

import model.conditioncorrector.ExpressionMatch;
import model.exercise.Success;
import model.matching.ScalaMatch;
import model.matching.ScalaMatchingResult;
import model.result.EvaluationResult;
import net.sf.jsqlparser.expression.Expression;

case class ScalaSqlResult(learnerSolution: String,
    columnComparison: ScalaMatchingResult[ScalaColumnWrapper, ColumnMatch],
    whereComparison: ScalaMatchingResult[Expression, ExpressionMatch],
    executionResult: SqlExecutionResult,
    tableComparison: ScalaMatchingResult[String, ScalaMatch[String]],
    otherComparisons: List[ScalaMatchingResult[_, _ <: ScalaMatch[_]]]) extends EvaluationResult(Success.NONE) {

  def getScalaMatchingResults(): List[ScalaMatchingResult[_, _ <: ScalaMatch[_]]] = List(columnComparison, tableComparison, whereComparison) ++ otherComparisons

  def getResults() = List(columnComparison, whereComparison, executionResult, tableComparison)

}

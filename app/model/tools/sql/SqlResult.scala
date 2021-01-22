package model.tools.sql

import model.matching.StringMatcher.StringMatchingResult
import model.points._
import model.result.{AbstractCorrectionResult, InternalErrorResult}
import model.tools.sql.SqlTool._

final case class WrongStatementTypeException(awaited: String, gotten: String)
    extends Exception(s"Wrong type of statement! Expected '$awaited', bot got '$gotten'")

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

final case class SelectAdditionalComparisons(
  groupByComparison: StringMatchingResult,
  orderByComparison: StringMatchingResult,
  limitComparison: LimitComparison
) {

  def points: Points = groupByComparison.points + orderByComparison.points + limitComparison.points

  def maxPoints: Points = groupByComparison.maxPoints + orderByComparison.maxPoints + limitComparison.maxPoints

}

final case class AdditionalComparison(
  selectComparisons: Option[SelectAdditionalComparisons],
  insertComparison: Option[StringMatchingResult]
) {

  def points: Points = {
    val selPoints = selectComparisons.map(_.points).getOrElse(zeroPoints)
    val insPoints = insertComparison.map(_.points).getOrElse(zeroPoints)

    selPoints + insPoints
  }

  def maxPoints: Points = {
    val selPoints = selectComparisons.map(_.maxPoints).getOrElse(zeroPoints)
    val insPoints = insertComparison.map(_.maxPoints).getOrElse(zeroPoints)

    selPoints + insPoints
  }

}

final case class SqlQueriesStaticComparison(
  columnComparison: ColumnComparison,
  tableComparison: StringMatchingResult,
  joinExpressionComparison: BinaryExpressionComparison,
  whereComparison: BinaryExpressionComparison,
  additionalComparisons: AdditionalComparison
) {

  val points: Points = columnComparison.points +
    tableComparison.points +
    joinExpressionComparison.points +
    whereComparison.points +
    additionalComparisons.points

  val maxPoints: Points = columnComparison.maxPoints +
    tableComparison.maxPoints +
    joinExpressionComparison.maxPoints +
    whereComparison.maxPoints +
    additionalComparisons.maxPoints

}

// Complete result

sealed trait SqlAbstractResult extends AbstractCorrectionResult

final case class SqlInternalErrorResult(
  msg: String,
  maxPoints: Points = (-1).points
) extends SqlAbstractResult
    with InternalErrorResult

final case class SqlResult(
  staticComparison: SqlQueriesStaticComparison,
  executionResult: SqlExecutionResult
) extends SqlAbstractResult {

  override def points: Points = staticComparison.points

  override def maxPoints: Points = staticComparison.maxPoints

  override def isCompletelyCorrect: Boolean = points == maxPoints

}

final case class SqlExecutionResult(
  userResult: Option[SqlQueryResult],
  sampleResult: Option[SqlQueryResult]
)

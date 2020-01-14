package model.tools.collectionTools.sql

import model.core.result.{CompleteResult, EvaluationResult, SuccessType}
import model.points._
import model.tools.collectionTools.sql.SqlToolMain._

final case class WrongStatementTypeException(awaited: String, gotten: String) extends Exception(s"Wrong type of statement! Expected '$awaited', bot got '$gotten'")

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

  val successType: SuccessType

}


final case class SelectAdditionalComparisons(
  groupByComparison: GroupByComparison,
  orderByComparison: OrderByComparison,
  limitComparison: LimitComparison
)

final case class AdditionalComparison(
  selectComparisons: Option[SelectAdditionalComparisons],
  insertComparison: Option[InsertComparison]
) {

  def points: Points = ???

  def maxPoints: Points = ???

  def results: Seq[EvaluationResult] = selectComparisons.map(_ => ???).getOrElse[Seq[EvaluationResult]](???) ++ insertComparison

}


@deprecated
final case class SqlQueriesStaticComparison[Q](
  userQ: Q, sampleQ: Q,
  columnComparison: ColumnComparison,
  tableComparison: TableComparison,
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

// FIXME: use builder?
final case class SqlResult(
  columnComparison: ColumnComparison,
  tableComparison: TableComparison,
  joinExpressionComparison: BinaryExpressionComparison,
  whereComparison: BinaryExpressionComparison,
  additionalComparisons: AdditionalComparison,
  executionResult: SqlExecutionResult,
  solutionSaved: Boolean
) extends SqlCorrResult {

  override def results: Seq[EvaluationResult] = Seq(columnComparison, tableComparison, whereComparison, executionResult) ++ additionalComparisons.results

  override val successType: SuccessType = if (results.nonEmpty && results.forall(_.success == SuccessType.COMPLETE)) SuccessType.COMPLETE else SuccessType.PARTIALLY

  override val points: Points = columnComparison.points +
    tableComparison.points +
    joinExpressionComparison.points +
    whereComparison.points +
    additionalComparisons.points

  // FIXME: points for executionResult?

  override val maxPoints: Points = columnComparison.maxPoints +
    tableComparison.maxPoints +
    joinExpressionComparison.maxPoints +
    whereComparison.maxPoints +
    additionalComparisons.maxPoints

}

final case class SqlParseFailed(error: Throwable, maxPoints: Points, solutionSaved: Boolean) extends SqlCorrResult {

  override def results: Seq[EvaluationResult] = Seq[EvaluationResult]()

  override val successType: SuccessType = SuccessType.ERROR

  override val points: Points = zeroPoints

}

final case class SqlExecutionResult(
  userResultTry: Option[SqlQueryResult],
  sampleResultTry: Option[SqlQueryResult]
) extends EvaluationResult {

  override val success: SuccessType = userResultTry match {
    case None             => SuccessType.ERROR
    case Some(userResult) => sampleResultTry match {
      case None               => SuccessType.PARTIALLY
      case Some(sampleResult) => SuccessType.ofBool(userResult isIdentic sampleResult)
    }
  }

}

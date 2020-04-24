package model.tools.sql

import model.User
import model.core.matching.MatchingResult
import model.points._
import model.tools.sql.matcher._
import model.tools._
import net.sf.jsqlparser.expression.operators.relational.ExpressionList
import net.sf.jsqlparser.expression.{BinaryExpression, Expression}
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.select.{Limit, OrderByElement}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

object SqlTool extends CollectionTool("sql", "Sql") {

  private val correctorsAndDaos: Map[SqlExerciseType, (QueryCorrector, SqlExecutionDAO)] = Map(
    SqlExerciseType.SELECT -> ((SelectCorrector, SelectDAO)),
    SqlExerciseType.CREATE -> ((CreateCorrector, CreateDAO)),
    SqlExerciseType.UPDATE -> ((UpdateCorrector, ChangeDAO)),
    SqlExerciseType.INSERT -> ((InsertCorrector, ChangeDAO)),
    SqlExerciseType.DELETE -> ((DeleteCorrector, ChangeDAO))
  )

  // Abstract types

  override type SolType        = String
  override type ExContentType  = SqlExerciseContent
  override type PartType       = SqlExPart
  override type CompResultType = SqlAbstractResult

  type ColumnComparison           = MatchingResult[ColumnWrapper, ColumnMatch]
  type TableComparison            = MatchingResult[Table, TableMatch]
  type BinaryExpressionComparison = MatchingResult[BinaryExpression, BinaryExpressionMatch]

  type GroupByComparison = MatchingResult[Expression, GroupByMatch]
  type OrderByComparison = MatchingResult[OrderByElement, OrderByMatch]
  type LimitComparison   = MatchingResult[Limit, LimitMatch]

  type InsertComparison = MatchingResult[ExpressionList, ExpressionListMatch]

  // Yaml, Html forms, Json

  override val toolJsonProtocol: StringSampleSolutionToolJsonProtocol[SqlExerciseContent, SqlExPart] =
    SqlToolJsonProtocols

  override val graphQlModels: ToolGraphQLModelBasics[String, SqlExerciseContent, SqlExPart] =
    SqlGraphQLModels

  override val allTopics: Seq[Topic] = SqlTopics.values

  // Correction

  override def correctAbstract(
    user: User,
    solution: SolType,
    exercise: Exercise[String, SqlExerciseContent],
    part: SqlExPart,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[Try[SqlAbstractResult]] = Future {
    Success {
      correctorsAndDaos.get(exercise.content.exerciseType) match {
        case None => SqlInternalErrorResult("There has been an internal error", solutionSaved, (-1).points)
        case Some((corrector, dao)) =>
          corrector.correct(dao, exercise.content.schemaName, solution, exercise.content.sampleSolutions, solutionSaved)
      }
    }
  }

}

package model.tools.sql

import model.User
import model.core.matching.MatchingResult
import model.tools.sql.matcher._
import model.tools.{StringSampleSolutionToolJsonProtocol, _}
import net.sf.jsqlparser.expression.operators.relational.ExpressionList
import net.sf.jsqlparser.expression.{BinaryExpression, Expression}
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.select.{Limit, OrderByElement}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

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
  override type CompResultType = AbstractSqlResult

  type ColumnComparison           = MatchingResult[ColumnWrapper, ColumnMatch]
  type TableComparison            = MatchingResult[Table, TableMatch]
  type BinaryExpressionComparison = MatchingResult[BinaryExpression, BinaryExpressionMatch]

  type GroupByComparison = MatchingResult[Expression, GroupByMatch]
  type OrderByComparison = MatchingResult[OrderByElement, OrderByMatch]
  type LimitComparison   = MatchingResult[Limit, LimitMatch]

  type InsertComparison = MatchingResult[ExpressionList, ExpressionListMatch]

  // Yaml, Html forms, Json

  override val toolJsonProtocol: StringSampleSolutionToolJsonProtocol[SqlExerciseContent, SqlExPart] =
    SqlJsonProtocols

  override val graphQlModels: ToolGraphQLModelBasics[String, SqlExerciseContent, SqlExPart] =
    SqlGraphQLModels

  // Correction

  override def correctAbstract(
    user: User,
    learnerSolution: SolType,
    collection: ExerciseCollection,
    exercise: Exercise,
    exerciseContent: SqlExerciseContent,
    sampleSolutions: Seq[SampleSolution[String]],
    part: SqlExPart,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[Try[AbstractSqlResult]] = Future {
    correctorsAndDaos.get(exerciseContent.exerciseType) match {
      case None => Failure(new Exception(s"There is no corrector or sql dao for ${exerciseContent.exerciseType}"))
      case Some((corrector, dao)) =>
        corrector.correct(
          dao,
          learnerSolution,
          collection,
          sampleSolutions,
          solutionSaved
        )
    }
  }

}

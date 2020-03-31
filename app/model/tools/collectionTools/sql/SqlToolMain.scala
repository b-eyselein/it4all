package model.tools.collectionTools.sql

import model.User
import model.core.matching.MatchingResult
import model.tools.collectionTools._
import model.tools.collectionTools.sql.matcher._
import net.sf.jsqlparser.expression.operators.relational.ExpressionList
import net.sf.jsqlparser.expression.{BinaryExpression, Expression}
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.select.{Limit, OrderByElement}

import scala.collection.immutable.IndexedSeq
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

object SqlToolMain extends CollectionToolMain(SqlConsts) {

  private val correctorsAndDaos: Map[SqlExerciseType, (QueryCorrector, SqlExecutionDAO)] = Map(
    SqlExerciseType.SELECT -> ((SelectCorrector, SelectDAO)),
    SqlExerciseType.CREATE -> ((CreateCorrector, CreateDAO)),
    SqlExerciseType.UPDATE -> ((UpdateCorrector, ChangeDAO)),
    SqlExerciseType.INSERT -> ((InsertCorrector, ChangeDAO)),
    SqlExerciseType.DELETE -> ((DeleteCorrector, ChangeDAO))
  )

  // Abstract types

  override type PartType       = SqlExPart
  override type ExContentType  = SqlExerciseContent
  override type SolType        = String
  override type CompResultType = AbstractSqlResult

  type ColumnComparison           = MatchingResult[ColumnWrapper, ColumnMatch]
  type TableComparison            = MatchingResult[Table, TableMatch]
  type BinaryExpressionComparison = MatchingResult[BinaryExpression, BinaryExpressionMatch]

  type GroupByComparison = MatchingResult[Expression, GroupByMatch]
  type OrderByComparison = MatchingResult[OrderByElement, OrderByMatch]
  type LimitComparison   = MatchingResult[Limit, LimitMatch]

  type InsertComparison = MatchingResult[ExpressionList, ExpressionListMatch]

  // Members

  override val exParts: IndexedSeq[SqlExPart] = SqlExParts.values

  // Yaml, Html forms, Json

  override val toolJsonProtocol: StringSampleSolutionToolJsonProtocol[SqlExerciseContent, SqlExPart] =
    SqlJsonProtocols

  override val graphQlModels: ToolGraphQLModelBasics[SqlExerciseContent, String,  SqlExPart] =
    SqlGraphQLModels

  // Correction

  override protected def correctEx(
    user: User,
    learnerSolution: SolType,
    sqlScenario: ExerciseCollection,
    exercise: Exercise,
    content: SqlExerciseContent,
    part: SqlExPart,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[Try[AbstractSqlResult]] =
    correctorsAndDaos.get(content.exerciseType) match {
      case None =>
        Future.successful(Failure(new Exception(s"There is no corrector or sql dao for ${content.exerciseType}")))
      case Some((corrector, dao)) =>
        Future {
          corrector.correct(dao, learnerSolution, content, sqlScenario, solutionSaved)
        }
    }

}

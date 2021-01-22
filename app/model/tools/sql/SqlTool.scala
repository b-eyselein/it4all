package model.tools.sql

import initialData.InitialData
import initialData.sql.SqlInitialData
import model.graphql.ToolGraphQLModelBasics
import model.matching.MatchingResult
import model.tools._
import model.tools.sql.matcher._
import model.{Exercise, LoggedInUser, Topic}
import net.sf.jsqlparser.expression.BinaryExpression
import net.sf.jsqlparser.statement.select.Limit

import scala.concurrent.{ExecutionContext, Future}

object SqlTool extends Tool("sql", "Sql") {

  private val correctorsAndDaos: Map[SqlExerciseType, (QueryCorrector, SqlExecutionDAO)] = Map(
    SqlExerciseType.SELECT -> ((SelectCorrector, SelectDAO)),
    SqlExerciseType.CREATE -> ((CreateCorrector, CreateDAO)),
    SqlExerciseType.UPDATE -> ((UpdateCorrector, ChangeDAO)),
    SqlExerciseType.INSERT -> ((InsertCorrector, ChangeDAO)),
    SqlExerciseType.DELETE -> ((DeleteCorrector, ChangeDAO))
  )

  // Abstract types

  override type SolType       = String
  override type ExContentType = SqlExerciseContent
  override type PartType      = SqlExPart
  override type ResType       = SqlAbstractResult

  type SqlExercise = Exercise[SqlExerciseContent]

  type ColumnComparison           = MatchingResult[ColumnWrapper, ColumnMatch]
  type BinaryExpressionComparison = MatchingResult[BinaryExpression, BinaryExpressionMatch]

  type LimitComparison = MatchingResult[Limit, LimitMatch]

  // Yaml, Html forms, Json

  override val jsonFormats: StringSampleSolutionToolJsonProtocol[SqlExerciseContent, SqlExPart] =
    SqlToolJsonProtocols

  override val graphQlModels: ToolGraphQLModelBasics[String, SqlExerciseContent, SqlExPart, SqlAbstractResult] =
    SqlGraphQLModels

  override val allTopics: Seq[Topic] = SqlTopics.values

  // Correction

  override def correctAbstract(
    user: LoggedInUser,
    solution: SolType,
    exercise: SqlExercise,
    part: SqlExPart
  )(implicit executionContext: ExecutionContext): Future[SqlAbstractResult] = Future {
    correctorsAndDaos.get(exercise.content.exerciseType) match {
      case None => SqlInternalErrorResult("There has been an internal error")
      case Some((corrector, dao)) =>
        corrector.correct(dao, exercise.content.schemaName, solution, exercise.content.sampleSolutions)
    }
  }

  override val initialData: InitialData[SqlExerciseContent] = SqlInitialData

}

package model.tools.sql

import initialData.InitialData
import initialData.sql.SqlInitialData
import model.graphql.ToolGraphQLModelBasics
import model.matching.MatchingResult
import model.tools._
import model.tools.sql.matcher._
import model.{Exercise, LoggedInUser, Topic}
import net.sf.jsqlparser.expression.BinaryExpression

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object SqlTool extends Tool("sql", "Sql") {

  // Abstract types

  override type SolutionInputType = String
  override type ExContentType     = SqlExerciseContent
  override type PartType          = SqlExPart
  override type ResType           = SqlResult

  type SqlExercise = Exercise[SqlExerciseContent]

  type ColumnComparison           = MatchingResult[ColumnWrapper, ColumnMatch]
  type BinaryExpressionComparison = MatchingResult[BinaryExpression, BinaryExpressionMatch]

  // Yaml, Html forms, Json

  override val jsonFormats: StringSolutionToolJsonProtocol[SqlExerciseContent, SqlExPart] = SqlToolJsonProtocols

  override val graphQlModels: ToolGraphQLModelBasics[String, SqlExerciseContent, SqlExPart, SqlResult] = SqlGraphQLModels

  override val allTopics: Seq[Topic] = SqlTopics.values

  // Correction

  override def correctAbstract(
    user: LoggedInUser,
    solution: String,
    exercise: SqlExercise,
    part: SqlExPart
  )(implicit executionContext: ExecutionContext): Future[Try[SqlResult]] = Future {

    val corrector = exercise.content.exerciseType match {
      case SqlExerciseType.SELECT => SelectCorrector
      case SqlExerciseType.CREATE => CreateCorrector
      case SqlExerciseType.UPDATE => UpdateCorrector
      case SqlExerciseType.INSERT => InsertCorrector
      case SqlExerciseType.DELETE => DeleteCorrector
    }

    val x = corrector.correct(exercise.content.schemaName, solution, exercise.content.sampleSolutions)

    println(x)

    x
  }

  override val initialData: InitialData[SqlExerciseContent] = SqlInitialData

}

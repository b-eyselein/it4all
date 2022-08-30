package model.tools.sql

import initialData.InitialData
import initialData.sql.SqlInitialData
import model.graphql.ToolGraphQLModelBasics
import model.matching.MatchingResult
import model.tools._
import model.tools.sql.matcher._
import model.{Exercise, Topic, User}
import net.sf.jsqlparser.expression.BinaryExpression

import scala.concurrent.{ExecutionContext, Future}

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
    user: User,
    solution: String,
    exercise: SqlExercise,
    part: SqlExPart
  )(implicit executionContext: ExecutionContext): Future[SqlResult] = {

    val corrector = exercise.content.exerciseType match {
      case SqlExerciseType.SELECT => SelectCorrector
      case SqlExerciseType.CREATE => CreateCorrector
      case SqlExerciseType.UPDATE => UpdateCorrector
      case SqlExerciseType.INSERT => InsertCorrector
      case SqlExerciseType.DELETE => DeleteCorrector
    }

    Future.fromTry(corrector.correct(exercise.content.schemaName, solution, exercise.content.sampleSolutions))
  }

  override val initialData: InitialData[SqlExerciseContent] = SqlInitialData

}

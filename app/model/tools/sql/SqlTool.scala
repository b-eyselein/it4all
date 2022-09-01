package model.tools.sql

import initialData.InitialData
import initialData.sql.SqlInitialData
import model.graphql.ToolWithoutPartsGraphQLModel
import model.matching.MatchingResult
import model.tools._
import model.tools.sql.matcher._
import model.{Exercise, User}
import net.sf.jsqlparser.expression.BinaryExpression
import play.api.libs.json.{Json, OFormat}

import scala.concurrent.{ExecutionContext, Future}

//noinspection ScalaFileName
object SqlToolJsonProtocols extends StringSolutionToolJsonProtocol[SqlExerciseContent] {

  override val exerciseContentFormat: OFormat[SqlExerciseContent] = Json.format

}

object SqlTool extends ToolWithoutParts("sql", "Sql") {

  // Abstract types

  override type SolInputType = String
  override type ExContType     = SqlExerciseContent
  override type ResType           = SqlResult

  type SqlExercise = Exercise[SqlExerciseContent]

  type ColumnComparison           = MatchingResult[ColumnWrapper, ColumnMatch]
  type BinaryExpressionComparison = MatchingResult[BinaryExpression, BinaryExpressionMatch]

  // Yaml, Html forms, Json

  override val jsonFormats: StringSolutionToolJsonProtocol[SqlExerciseContent] = SqlToolJsonProtocols

  override val graphQlModels: ToolWithoutPartsGraphQLModel[String, SqlExerciseContent, SqlResult] = SqlGraphQLModels

  // Correction

  override def correctAbstract(user: User, solution: String, exercise: SqlExercise)(implicit executionContext: ExecutionContext): Future[SqlResult] = {

    val corrector = exercise.content.exerciseType match {
      case SqlExerciseType.SELECT => SelectCorrector
      case SqlExerciseType.CREATE => CreateCorrector
      case SqlExerciseType.UPDATE => UpdateCorrector
      case SqlExerciseType.INSERT => InsertCorrector
      case SqlExerciseType.DELETE => DeleteCorrector
    }

    Future.fromTry {
      corrector.correct(exercise.content.schemaName, solution, exercise.content.sampleSolutions)
    }
  }

  override val initialData: InitialData[SqlExerciseContent] = SqlInitialData.initialData

}

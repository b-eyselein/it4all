package model.tools.flask

import initialData.InitialData
import initialData.flask.FlaskInitialData
import model.graphql.FilesSolutionToolGraphQLModelBasics
import model.tools.{FilesSolutionToolJsonProtocol, ToolWithParts}
import model.{Exercise, FilesSolutionInput, User}

import scala.concurrent.{ExecutionContext, Future}

object FlaskTool extends ToolWithParts("flask", "Flask", true) {

  override type SolInputType = FilesSolutionInput
  override type ExContType     = FlaskExerciseContent
  override type PartType          = FlaskExPart
  override type ResType           = FlaskResult

  type FlaskExercise = Exercise[FlaskExerciseContent]

  override val jsonFormats: FilesSolutionToolJsonProtocol[FlaskExerciseContent, FlaskExPart] = FlaskToolJsonProtocol

  override val graphQlModels: FilesSolutionToolGraphQLModelBasics[FlaskExerciseContent, FlaskResult, FlaskExPart] = FlaskToolGraphQLModels

  override def correctAbstract(
    user: User,
    solution: FilesSolutionInput,
    exercise: Exercise[FlaskExerciseContent],
    part: FlaskExPart
  )(implicit executionContext: ExecutionContext): Future[FlaskResult] =
    FlaskCorrector.correct(solution, exercise, solutionDirForExercise(user.username, exercise.collectionId, exercise.exerciseId))

  override val initialData: InitialData[FlaskExerciseContent] = FlaskInitialData.initialData

}

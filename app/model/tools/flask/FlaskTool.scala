package model.tools.flask

import initialData.InitialData
import initialData.flask.FlaskInitialData
import model.tools.ToolWithoutParts
import model.{Exercise, FilesSolutionInput, User}

import scala.concurrent.{ExecutionContext, Future}

object FlaskTool extends ToolWithoutParts("flask", "Flask", true) {

  override type SolInputType = FilesSolutionInput
  override type ExContType   = FlaskExerciseContent
  override type ResType      = FlaskResult

  type FlaskExercise = Exercise[FlaskExerciseContent]

  // noinspection TypeAnnotation
  override val jsonFormats = FlaskToolJsonProtocol

  // noinspection TypeAnnotation
  override val graphQlModels = FlaskToolGraphQLModels

  override def correctAbstract(
    user: User,
    solution: FilesSolutionInput,
    exercise: Exercise[FlaskExerciseContent]
  )(implicit executionContext: ExecutionContext): Future[FlaskResult] = FlaskCorrector.correct(
    solution,
    exercise,
    solutionDirForExercise(user.username, exercise.collectionId, exercise.exerciseId)
  )

  override val initialData: InitialData[FlaskExerciseContent] = FlaskInitialData.initialData

}

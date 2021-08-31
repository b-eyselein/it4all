package model.tools.flask

import initialData.InitialData
import initialData.flask.FlaskInitialData
import model.graphql.FilesSolutionToolGraphQLModelBasics
import model.tools.{FilesSolutionToolJsonProtocol, Tool, ToolState}
import model.{Exercise, FilesSolutionInput, LoggedInUser}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object FlaskTool extends Tool("flask", "Flask", ToolState.ALPHA) {

  override type SolutionInputType = FilesSolutionInput
  override type ExContentType     = FlaskExerciseContent
  override type PartType          = FlaskExPart
  override type ResType           = FlaskResult

  type FlaskExercise = Exercise[FlaskExerciseContent]

  override val jsonFormats: FilesSolutionToolJsonProtocol[FlaskExerciseContent, FlaskExPart] = FlaskToolJsonProtocol

  override val graphQlModels: FilesSolutionToolGraphQLModelBasics[FlaskExerciseContent, FlaskExPart, FlaskResult] = FlaskToolGraphQLModels

  override def correctAbstract(
    user: LoggedInUser,
    solution: FilesSolutionInput,
    exercise: Exercise[FlaskExerciseContent],
    part: FlaskExPart
  )(implicit executionContext: ExecutionContext): Future[Try[FlaskResult]] = FlaskCorrector.correct(user.username, solution, exercise)

  override val initialData: InitialData[FlaskExerciseContent] = FlaskInitialData

}

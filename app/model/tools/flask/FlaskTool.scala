package model.tools.flask

import initialData.InitialData
import initialData.flask.FlaskInitialData
import model.graphql.FilesSolutionToolGraphQLModelBasics
import model.tools.{FilesSampleSolutionToolJsonProtocol, Tool, ToolState}
import model.{Exercise, FilesSolution, LoggedInUser}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object FlaskTool extends Tool("flask", "Flask", ToolState.ALPHA) {

  override type SolType       = FilesSolution
  override type ExContentType = FlaskExerciseContent
  override type PartType      = FlaskExPart
  override type ResType       = FlaskAbstractResult

  type FlaskExercise = Exercise[FlaskExerciseContent]

  override val jsonFormats: FilesSampleSolutionToolJsonProtocol[FlaskExerciseContent, FlaskExPart] =
    FlaskToolJsonProtocol

  override val graphQlModels
    : FilesSolutionToolGraphQLModelBasics[FlaskExerciseContent, FlaskExPart, FlaskAbstractResult] =
    FlaskToolGraphQLModels

  override def correctAbstract(
    user: LoggedInUser,
    solution: FilesSolution,
    exercise: Exercise[FlaskExerciseContent],
    part: FlaskExPart
  )(implicit executionContext: ExecutionContext): Future[Try[FlaskAbstractResult]] =
    FlaskCorrector.correct(user.username, solution, exercise)

  override val initialData: InitialData[FlaskExerciseContent] = FlaskInitialData

}

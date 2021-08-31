package model.tools.rose

import initialData.InitialData
import initialData.rose.RoseInitialData
import model.graphql.ToolGraphQLModelBasics
import model.tools._
import model.{Exercise, LoggedInUser}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object RoseTool extends Tool("rose", "Rose", ToolState.PRE_ALPHA) {

  override type SolutionInputType = String
  override type ExContentType     = RoseExerciseContent
  override type PartType          = RoseExPart
  override type ResType           = RoseResult

  type RoseExercise = Exercise[RoseExerciseContent]

  // Yaml, Html forms, Json

  override val jsonFormats: StringSolutionToolJsonProtocol[RoseExerciseContent, RoseExPart] = RoseToolJsonProtocol

  override val graphQlModels: ToolGraphQLModelBasics[String, RoseExerciseContent, RoseExPart, RoseResult] = RoseGraphQLModels

  // Correction

  override def correctAbstract(
    user: LoggedInUser,
    solution: String,
    exercise: RoseExercise,
    part: RoseExPart
  )(implicit executionContext: ExecutionContext): Future[Try[RoseResult]] = {

    val solDir = solutionDirForExercise(user.username, exercise.collectionId, exercise.exerciseId)

    RoseCorrector.correct(solution, exercise, solDir)
  }

  override val initialData: InitialData[RoseExerciseContent] = RoseInitialData

}

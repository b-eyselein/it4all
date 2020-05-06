package model.tools.rose

import initialData.InitialData
import initialData.rose.RoseInitialData
import model.graphql.ToolGraphQLModelBasics
import model.tools._
import model.tools.programming.ProgLanguages
import model.{Exercise, LoggedInUser}

import scala.concurrent.{ExecutionContext, Future}

object RoseTool extends CollectionTool("rose", "Rose", ToolState.PRE_ALPHA) {

  override type SolType       = String
  override type ExContentType = RoseExerciseContent
  override type PartType      = RoseExPart
  override type ResType       = RoseAbstractResult

  type RoseExercise = Exercise[String, RoseExerciseContent]

  // Yaml, Html forms, Json

  override val jsonFormats: ToolJsonProtocol[String, RoseExerciseContent, RoseExPart] =
    RoseToolJsonProtocol

  override val graphQlModels: ToolGraphQLModelBasics[String, RoseExerciseContent, RoseExPart, RoseAbstractResult] =
    RoseGraphQLModels

  // Correction

  override def correctAbstract(
    user: LoggedInUser,
    solution: String,
    exercise: RoseExercise,
    part: RoseExPart,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[RoseAbstractResult] = {

    val solDir = solutionDirForExercise(user.username, exercise.collectionId, exercise.exerciseId)

    RoseCorrector.correct(solution, exercise, ProgLanguages.StandardLanguage, solDir, solutionSaved)
  }

  override val initialData: InitialData[String, RoseExerciseContent] = RoseInitialData

}

package model.tools.rose

import model.User
import model.tools._
import model.tools.programming.ProgLanguages

import scala.concurrent.{ExecutionContext, Future}

object RoseTool extends CollectionTool("rose", "Rose", ToolState.PRE_ALPHA) {

  override type SolType       = String
  override type ExContentType = RoseExerciseContent
  override type PartType      = RoseExPart
  override type ResType       = RoseAbstractResult

  // Yaml, Html forms, Json

  override val toolJsonProtocol: ToolJsonProtocol[String, RoseExerciseContent, RoseExPart] =
    RoseToolJsonProtocol

  override val graphQlModels: ToolGraphQLModelBasics[String, RoseExerciseContent, RoseExPart, RoseAbstractResult] =
    RoseGraphQLModels

  // Correction

  override def correctAbstract(
    user: User,
    solution: String,
    exercise: Exercise[String, RoseExerciseContent],
    part: RoseExPart,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[RoseAbstractResult] = {

    val solDir = solutionDirForExercise(user.username, exercise.collectionId, exercise.exerciseId)

    RoseCorrector.correct(solution, exercise, ProgLanguages.StandardLanguage, solDir, solutionSaved)
  }

}

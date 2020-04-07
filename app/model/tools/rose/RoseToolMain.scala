package model.tools.rose

import model.User
import model.tools.programming.ProgLanguages
import model.tools._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

object RoseToolMain extends CollectionToolMain("rose", "Rose", ToolState.PRE_ALPHA) {

  override type PartType       = RoseExPart
  override type ExContentType  = RoseExerciseContent
  override type SolType        = String
  override type CompResultType = RoseCompleteResult

  // Other members

  override val exParts: Seq[RoseExPart] = RoseExParts.values

  // Yaml, Html forms, Json

  override val toolJsonProtocol: ToolJsonProtocol[RoseExerciseContent, String, RoseExPart] =
    RoseToolJsonProtocol

  override val graphQlModels: ToolGraphQLModelBasics[RoseExerciseContent, String, RoseExPart] =
    RoseGraphQLModels

  // Correction

  override protected def correctEx(
    user: User,
    sol: String,
    collection: ExerciseCollection,
    exercise: Exercise,
    content: RoseExerciseContent,
    part: RoseExPart,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[Try[RoseCompleteResult]] =
    content.sampleSolutions.headOption match {
      case None => Future.successful(Failure(new Exception("No sample solution could be found!")))
      case Some(sampleSolution) =>
        RoseCorrector.correct(
          user,
          content,
          sol,
          sampleSolution.sample,
          ProgLanguages.StandardLanguage,
          solutionDirForExercise(user.username, collection.id, exercise.id),
          solutionSaved
        )
    }
}

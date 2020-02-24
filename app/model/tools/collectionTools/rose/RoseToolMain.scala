package model.tools.collectionTools.rose

import model.User
import model.tools.collectionTools.programming.ProgLanguages
import model.tools.collectionTools.{CollectionToolMain, Exercise, ExerciseCollection, ToolJsonProtocol}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

object RoseToolMain extends CollectionToolMain(RoseConsts) {

  override type PartType       = RoseExPart
  override type ExContentType  = RoseExerciseContent
  override type SolType        = String
  override type CompResultType = RoseCompleteResult

  // Other members

  override val exParts: Seq[RoseExPart] = RoseExParts.values

  // Yaml, Html forms, Json

  override protected val toolJsonProtocol: ToolJsonProtocol[RoseExerciseContent, String, RoseCompleteResult] =
    RoseToolJsonProtocol

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

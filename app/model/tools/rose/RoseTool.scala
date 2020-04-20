package model.tools.rose

import model.User
import model.tools._
import model.tools.programming.ProgLanguages

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

object RoseTool extends CollectionTool("rose", "Rose", ToolState.PRE_ALPHA) {

  override type SolType        = String
  override type ExContentType  = RoseExerciseContent
  override type PartType       = RoseExPart
  override type CompResultType = RoseCompleteResult

  // Yaml, Html forms, Json

  override val toolJsonProtocol: ToolJsonProtocol[String, RoseExerciseContent, RoseExPart] =
    RoseToolJsonProtocol

  override val graphQlModels: ToolGraphQLModelBasics[String, RoseExerciseContent, RoseExPart] =
    RoseGraphQLModels

  // Correction

  override def correctAbstract(
    user: User,
    sol: String,
    collection: ExerciseCollection,
    exercise: Exercise[String, RoseExerciseContent],
    part: RoseExPart,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[Try[RoseCompleteResult]] =
    exercise.content.sampleSolutions.headOption match {
      case None => Future.successful(Failure(new Exception("No sample solution could be found!")))
      case Some(sampleSolution) =>
        RoseCorrector.correct(
          sol,
          sampleSolution.sample,
          ProgLanguages.StandardLanguage,
          solutionDirForExercise(user.username, collection.id, exercise.id),
          solutionSaved
        )
    }

}

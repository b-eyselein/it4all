package model.tools.rose

import model.User
import model.persistence.DbExercise
import model.tools._
import model.tools.programming.ProgLanguages
import play.api.libs.json.Reads

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

object RoseTool extends CollectionTool("rose", "Rose", ToolState.PRE_ALPHA) {

  override type SolType        = String
  override type ExContentType  = RoseExerciseContent
  override type ExerciseType   = RoseExercise
  override type PartType       = RoseExPart
  override type CompResultType = RoseCompleteResult

  // Yaml, Html forms, Json

  override val toolJsonProtocol: ToolJsonProtocol[String, RoseExerciseContent, RoseExercise, RoseExPart] =
    RoseToolJsonProtocol

  override val graphQlModels: ToolGraphQLModelBasics[String, RoseExerciseContent, RoseExercise, RoseExPart] =
    RoseGraphQLModels

  // Correction

  override def correctAbstract(
    user: User,
    sol: String,
    collection: ExerciseCollection,
    exercise: RoseExercise,
    part: RoseExPart,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[Try[RoseCompleteResult]] =
    exercise.sampleSolutions.headOption match {
      case None => Future.successful(Failure(new Exception("No sample solution could be found!")))
      case Some(sampleSolution) =>
        RoseCorrector.correct(
          user,
          exercise,
          sol,
          sampleSolution.sample,
          ProgLanguages.StandardLanguage,
          solutionDirForExercise(user.username, collection.id, exercise.id),
          solutionSaved
        )
    }

  override protected def convertExerciseFromDb(dbExercise: DbExercise, topics: Seq[Topic]): Option[RoseExercise] =
    dbExercise match {
      case DbExercise(id, collectionId, toolId, title, authors, text, difficulty, sampleSolutionsJson, contentJson) =>
        for {
          sampleSolutions <- Reads.seq(toolJsonProtocol.sampleSolutionFormat).reads(sampleSolutionsJson).asOpt
          content         <- toolJsonProtocol.exerciseContentFormat.reads(contentJson).asOpt
        } yield RoseExercise(
          id,
          collectionId,
          toolId,
          title,
          authors,
          text,
          topics,
          difficulty,
          sampleSolutions,
          content
        )
    }

}

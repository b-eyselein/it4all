package model.tools.programming

import model.User
import model.persistence.DbExercise
import model.tools._
import play.api.libs.json.{Reads, Writes}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object ProgTool extends CollectionTool("programming", "Programmierung", ToolState.ALPHA) {

  override type SolType        = ProgSolution
  override type ExContentType  = ProgExerciseContent
  override type ExerciseType   = ProgrammingExercise
  override type PartType       = ProgExPart
  override type CompResultType = ProgCompleteResult

  // Yaml, Html Forms, Json

  override val toolJsonProtocol: ToolJsonProtocol[ProgSolution, ProgExerciseContent, ProgrammingExercise, ProgExPart] =
    ProgrammingToolJsonProtocol

  override val graphQlModels
    : ToolGraphQLModelBasics[ProgSolution, ProgExerciseContent, ProgrammingExercise, ProgExPart] =
    ProgrammingGraphQLModels

  // Correction

  override def correctAbstract(
    user: User,
    sol: ProgSolution,
    collection: ExerciseCollection,
    exercise: ProgrammingExercise,
    part: ProgExPart,
    solutionSaved: Boolean
  )(implicit ec: ExecutionContext): Future[Try[ProgCompleteResult]] =
    ProgCorrector.correct(user, sol, exercise, part, solutionSaved)

  override protected def convertExerciseFromDb(dbExercise: DbExercise): Option[ProgrammingExercise] = dbExercise match {
    case DbExercise(id, collectionId, toolId, title, authors, text, difficulty, sampleSolutionsJson, contentJson) =>
      val topics: Seq[Topic] = ???

      for {
        sampleSolutions              <- Reads.seq(toolJsonProtocol.sampleSolutionFormat).reads(sampleSolutionsJson).asOpt
        content: ProgExerciseContent <- toolJsonProtocol.exerciseContentFormat.reads(contentJson).asOpt
      } yield ProgrammingExercise(
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

  override protected def convertExerciseToDb(exercise: ProgrammingExercise): DbExercise = exercise match {
    case ProgrammingExercise(
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
        ) =>
      DbExercise(
        id,
        collectionId,
        toolId,
        title,
        authors,
        text,
        difficulty,
        Writes.seq(toolJsonProtocol.sampleSolutionFormat).writes(sampleSolutions),
        toolJsonProtocol.exerciseContentFormat.writes(content)
      )
  }

}

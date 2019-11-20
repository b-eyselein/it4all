package model.persistence

import model._
import model.tools.collectionTools.{ExPart, FileExercise}

import scala.concurrent.Future

trait FilesSolutionExerciseTableQueries[PartType <: ExPart, ExType <: FileExercise, ReviewType <: ExerciseReview] {
  self: FilesSolutionExerciseTableDefs[PartType, ExType, ReviewType] =>

  import profile.api._

  // Reading user solution

  private def completeUserSolForDbUserSol(dbUserSol: DbFilesUserSolution[PartType]): Future[FilesUserSolution[PartType]] = for {
    dbUserSolFiles <- db.run(userSolutionFilesTableQuery
      .filter { f =>
        f.solId === dbUserSol.id && f.exId === dbUserSol.exId && f.collId === dbUserSol.collId && f.username === dbUserSol.username && f.part === dbUserSol.part
      }
      .result)
  } yield FilesSolutionDbModels.userSolFromDbUserSol[PartType](dbUserSol, dbUserSolFiles)

  def futureMaybeOldSolution(username: String, scenarioId: Int, exerciseId: Int, part: PartType): Future[Option[FilesUserSolution[PartType]]] = {
    val futureMaybeOldBaseUserSolution: Future[Option[DbFilesUserSolution[PartType]]] = db.run(
      userSolutionsTableQuery
        .filter { sol => sol.username === username && sol.collectionId === scenarioId && sol.exerciseId === exerciseId && sol.part === part }
        .sortBy(_.id.desc) // take last sample sol (with highest id)
        .result
        .headOption)

    futureMaybeOldBaseUserSolution.flatMap {
      case None                 => Future.successful(None)
      case Some(dbUserSolution) => completeUserSolForDbUserSol(dbUserSolution).map(Some.apply)
    }
  }

  // Saving user solution

  private def futureCurrentUserSolutionId(exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, part: PartType): Future[Option[Int]] =
    db.run(userSolutionsTableQuery
      .filter { s => s.exerciseId === exId && s.exSemVer === exSemVer && s.collectionId === collId && s.username === username && s.part === part }
      .sortBy(_.id.desc) // take highest id
      .map(_.id)
      .result
      .headOption
    )

  def futureSaveUserSolution(exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, sol: FilesUserSolution[PartType]): Future[Boolean] =
    futureCurrentUserSolutionId(exId, exSemVer, collId, username, sol.part).flatMap { maybeCurrentUserSolutionId =>

      val nextUserSolutionId = maybeCurrentUserSolutionId.getOrElse(0) + 1

      val (dbSolution, dbSolutionFiles) = FilesSolutionDbModels.dbUserSolFromUserSol(exId, exSemVer, collId, username, sol.copy(id = nextUserSolutionId))

      db.run(userSolutionsTableQuery += dbSolution).flatMap {
        case 1 => saveSeq[DbFilesUserSolutionFile[PartType]](dbSolutionFiles, dbuf => db.run(userSolutionFilesTableQuery += dbuf))
        case _ => Future.successful(false)
      }
    }

}

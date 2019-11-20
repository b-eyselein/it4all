package model.tools.collectionTools.programming.persistence

import model.SemanticVersion
import model.tools.collectionTools.programming._

import scala.concurrent.Future

trait ProgTableQueries {
  self: ProgTableDefs =>

  import profile.api._


  override def completeExForEx(collId: Int, ex: ProgExercise): Future[ProgExercise] = Future.successful(ex)

  override def saveExerciseRest(collId: Int, ex: ProgExercise): Future[Boolean] = Future.successful(true)

  private def futureUserSolutionById(id: Int, exId: Int, collId: Int, username: String, part: ProgExPart): Future[Option[ProgUserSolution]] =
    for {
      maybeDbUserSol <- db.run(userSolutionsTableQuery
        .filter { sol => sol.username === username && sol.collectionId === collId && sol.exerciseId === exId && sol.part === part && sol.id === id }
        .result.headOption)
      dbUserSolFiles <- db.run(userSolutionFilesTableQuery
        .filter { sf => sf.username === username && sf.solutionId === id && sf.exId === exId && sf.collId === collId && sf.part === part }
        .result)
    } yield {
      maybeDbUserSol.map { dbUserSol =>
        ProgSolutionDbModels.userSolFromDbUserSol(dbUserSol, dbUserSolFiles)
      }
    }


  def futureMaybeOldSolution(username: String, collectionId: Int, exerciseId: Int, part: ProgExPart): Future[Option[ProgUserSolution]] =
    maybeOldLastSolutionId(exerciseId, collectionId, username, part).flatMap {
      case None        => Future.successful(None)
      case Some(oldId) => futureUserSolutionById(oldId, exerciseId, collectionId, username, part)
    }


  def futureSaveUserSolution(exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, sol: ProgUserSolution): Future[Boolean] =
    nextUserSolutionId(exId, collId, username, sol.part).flatMap { nextSolId =>
      val dbUserSolWithFiles = ProgSolutionDbModels.dbUserSolFromUserSol(exId, exSemVer, collId, username, sol)

      val dbUserSol      = dbUserSolWithFiles._1.copy(id = nextSolId)
      val dbUserSolFiles = dbUserSolWithFiles._2.map(_.copy(solId = nextSolId))

      for {
        solSaved <- db.run(userSolutionsTableQuery += dbUserSol) transform(_ == 1, identity)
        solFilesSaved <- saveSeq[DbProgUserSolutionFile](dbUserSolFiles, i => db.run(userSolutionFilesTableQuery += i))
      } yield solSaved && solFilesSaved
    }

}

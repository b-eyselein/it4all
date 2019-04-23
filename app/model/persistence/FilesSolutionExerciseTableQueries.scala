package model.persistence

import model._

import scala.concurrent.Future

trait FilesSolutionExerciseTableQueries[PartType <: ExPart, ExType <: Exercise, CollType <: ExerciseCollection, ReviewType <: ExerciseReview] {
  self: FilesSolutionExerciseTableDefs[PartType, ExType, CollType, ReviewType] =>

  import profile.api._

  // Saving sample solution

  private def futureSaveSampleSolution(exId: Int, exSemVer: SemanticVersion, collId: Int, sampleSolution: FilesSampleSolution): Future[Boolean] = {
    val (dbSample, dbSampleFiles) = FilesSolutionDbModels.dbSampleSolFromSampleSol(exId, exSemVer, collId, sampleSolution)

    db.run(sampleSolutionsTableQuery += dbSample).flatMap {
      case 1 => saveSeq[DbFilesSampleSolutionFile](dbSampleFiles, dbsf => db.run(sampleSolutionFilesTableQuery += dbsf))
      case _ => Future.successful(false)
    }
  }

  protected def futureSaveSampleSolutions(exId: Int, exSemVer: SemanticVersion, collId: Int, sampleSolutions: Seq[FilesSampleSolution]): Future[Boolean] =
    saveSeq[FilesSampleSolution](sampleSolutions, futureSaveSampleSolution(exId, exSemVer, collId, _))

  // Reading sample solution

  private def completeSampleSolForDbDbSampleSol(dbSampleSol: DbFilesSampleSolution): Future[FilesSampleSolution] = for {
    dbSampleSolFiles <- db.run(sampleSolutionFilesTableQuery
      .filter { f => f.sampleId === dbSampleSol.id && f.exId === dbSampleSol.exId && f.exSemVer === dbSampleSol.exSemVer && f.collId === dbSampleSol.collId }
      .result)
  } yield FilesSolutionDbModels.sampleSolFromDbSampleSol(dbSampleSol, dbSampleSolFiles)

  protected def futureSampleSolutionsForExercise(collId: Int, exId: Int): Future[Seq[FilesSampleSolution]] = {
    db.run(sampleSolutionsTableQuery
      .filter { s => s.collectionId === collId && s.exerciseId === exId }
      .result)
      .flatMap { dbSampleSols: Seq[DbFilesSampleSolution] =>
        Future.sequence(dbSampleSols.map(completeSampleSolForDbDbSampleSol))
      }
  }

  override def futureSampleSolutionsForExPart(collId: Int, exId: Int, part: PartType): Future[Seq[FilesSampleSolution]] =
    db.run(
      sampleSolutionsTableQuery
        .filter { s => s.collectionId === collId && s.exerciseId === exId }
        .result
    )
      .flatMap { dbSampleSols: Seq[DbFilesSampleSolution] =>
        Future.sequence(dbSampleSols.map(completeSampleSolForDbDbSampleSol))
      }

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

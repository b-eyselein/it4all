package model.tools.programming.persistence

import model.persistence.DbExerciseFile
import model.tools.programming._
import model.{ExerciseFile, SemanticVersion}

import scala.concurrent.Future

trait ProgTableQueries {
  self: ProgTableDefs =>

  import profile.api._

  private def sampleSolutionsForExercise(ex: DbProgExercise): Future[Seq[ProgSampleSolution]] = for {
    dbSampleSols <- db.run(sampleSolutionsTableQuery.filter { s => s.exerciseId === ex.id && s.collectionId === ex.collectionId }.result)
    dbSampleSolFiles <- db.run(sampleSolutionFilesTableQuery.filter { sf => sf.exId === ex.id && sf.collId === ex.collectionId }.result)
  } yield {

    val groupedDbSampleSolFiles = dbSampleSolFiles.groupBy(_.solId)

    dbSampleSols.map { dbSampleSol: DbProgSampleSolution =>
      val filesForDbSampleSol = groupedDbSampleSolFiles.getOrElse(dbSampleSol.id, Seq.empty)
      ProgSolutionDbModels.sampleSolFromDbSampleSol(dbSampleSol, filesForDbSampleSol)
    }

  }

  private def implementationFilesForExercise(exercise: DbProgExercise): Future[Seq[ExerciseFile]] =
    db.run(
      implementationFilesTQ
        .filter { ipf => ipf.exerciseId === exercise.id && ipf.collectionId === exercise.collectionId }
        .result
    ).map(_.map(ProgDbModels.exerciseFileFromDbExerciseFile))

  override def completeExForEx(collId: Int, ex: DbProgExercise): Future[ProgExercise] = for {
    samples <- sampleSolutionsForExercise(ex)
    implementationFiles <- implementationFilesForExercise(ex)
  } yield ProgDbModels.exerciseFromDbValues(ex, samples, implementationFiles)

  override def saveExerciseRest(collId: Int, ex: ProgExercise): Future[Boolean] = {
    val dbSamplesWithFiles: (Seq[DbProgSampleSolution], Seq[Seq[DbProgSampleSolutionFile]]) = ex.sampleSolutions
      .map(s => ProgSolutionDbModels.dbSampleSolFromSampleSol(ex.id, ex.semanticVersion, collId, s))
      .unzip

    val dbSamples     = dbSamplesWithFiles._1
    val dbSampleFiles = dbSamplesWithFiles._2.flatten

    val dbImplementationFiles = ex.implementationPart.files
      .map(ipf => ProgDbModels.dbExerciseFileFromExerciseFile(ex.id, ex.semanticVersion, collId, ipf))

    for {
      samplesSaved <- saveSeq[DbProgSampleSolution](dbSamples, i => db.run(sampleSolutionsTableQuery += i))
      sampleFilesSaved <- saveSeq[DbProgSampleSolutionFile](dbSampleFiles, i => db.run(sampleSolutionFilesTableQuery += i))
      implementationFilesSaved <- saveSeq[DbExerciseFile](dbImplementationFiles, i => db.run(implementationFilesTQ += i))
    } yield samplesSaved && sampleFilesSaved && implementationFilesSaved
  }

  def futureSampleSolutionsForExPart(collId: Int, id: Int, part: ProgExPart): Future[Seq[ProgSampleSolution]] = futureExerciseById(collId, id).map {
    case None           => Seq.empty
    case Some(exercise) =>

      val filesToSendNames = part match {
        case ProgExParts.TestCreation => exercise.unitTestPart.sampleSolFileNames
        case _                        => exercise.implementationPart.sampleSolFileNames
      }

      exercise.sampleSolutions.map { case ProgSampleSolution(sampleId, ProgSolution(files, _)) =>
        val newFiles = files.filter(f => filesToSendNames.contains(f.name))
        ProgSampleSolution(sampleId, ProgSolution(newFiles, Seq.empty))
      }
  }

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

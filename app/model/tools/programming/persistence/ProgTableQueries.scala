package model.tools.programming.persistence

import model.persistence.DbExerciseFile
import model.tools.programming._
import model.tools.uml.UmlClassDiagram
import model.{ExerciseFile, SemanticVersion}

import scala.concurrent.{ExecutionContext, Future}

trait ProgTableQueries {
  self: ProgTableDefs =>

  protected implicit val executor: ExecutionContext

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

  private def inputTypeForExercise(collId: Int, ex: DbProgExercise): Future[Seq[ProgInput]] =
    db.run(inputTypesQuery.filter { it => it.exerciseId === ex.id && it.collectionId === collId }.result)
      .map(_.map(dbModels.progInputFromDbProgInput))

  private def sampleTestDataForExercise(collId: Int, ex: DbProgExercise): Future[Seq[ProgSampleTestData]] =
    db.run(sampleTestData.filter { st => st.exerciseId === ex.id && st.collectionId === collId }.result)
      .map(_.map(dbModels.sampleTestDataFromDbSampleTestData))

  private def unitTestTestConfigsForExercise(collId: Int, ex: DbProgExercise): Future[Seq[UnitTestTestConfig]] =
    db.run(unitTestTestConfigsTQ.filter { utc => utc.exerciseId === ex.id && utc.collectionId === collId }.result)
      .map(_.map(dbModels.unitTestTestConfigFromDbUnitTestTestConfig))

  private def unitTestFilesForExercise(collId: Int, ex: DbProgExercise): Future[Seq[ExerciseFile]] =
    db.run(
      progUnitTestFilesTQ
        .filter { wf => wf.exerciseId === ex.id && wf.collectionId === collId }
        .result
    ).map(_.map(ProgDbModels.exerciseFileFromDbExerciseFile))

  private def implementationFilesForExercise(exercise: DbProgExercise): Future[Seq[ExerciseFile]] =
    db.run(
      implementationFilesTQ
        .filter { ipf => ipf.exerciseId === exercise.id && ipf.collectionId === exercise.collectionId }
        .result
    ).map(_.map(ProgDbModels.exerciseFileFromDbExerciseFile))


  private def maybeClassDiagramPartForExercise(collId: Int, ex: DbProgExercise): Future[Option[UmlClassDiagram]] =
    db.run(umlClassDiagParts.filter { cdp => cdp.exerciseId === ex.id && cdp.collectionId === collId }.result.headOption)
      .map(_.map(_.classDiagram))

  override def completeExForEx(collId: Int, ex: DbProgExercise): Future[ProgExercise] = for {
    samples <- sampleSolutionsForExercise(ex)
    inputTypes <- inputTypeForExercise(collId, ex)
    sampleTestData <- sampleTestDataForExercise(collId, ex)
    unitTestTestConfigs <- unitTestTestConfigsForExercise(collId, ex)
    unitTestFiles <- unitTestFilesForExercise(collId, ex)
    implementationFiles <- implementationFilesForExercise(ex)
    maybeClassDiagram <- maybeClassDiagramPartForExercise(collId, ex)
  } yield dbModels.exerciseFromDbValues(ex, inputTypes, samples, sampleTestData, unitTestTestConfigs, unitTestFiles, implementationFiles, maybeClassDiagram)

  override def saveExerciseRest(collId: Int, ex: ProgExercise): Future[Boolean] = {
    val dbSamplesWithFiles: (Seq[DbProgSampleSolution], Seq[Seq[DbProgSampleSolutionFile]]) = ex.sampleSolutions
      .map(s => ProgSolutionDbModels.dbSampleSolFromSampleSol(ex.id, ex.semanticVersion, collId, s))
      .unzip

    val dbSamples = dbSamplesWithFiles._1
    val dbSampleFiles = dbSamplesWithFiles._2.flatten

    val dbProgInputs = ex.inputTypes
      .map(it => dbModels.dbProgInputFromProgInput(ex.id, ex.semanticVersion, collId, it))

    val dbSampleTestData = ex.sampleTestData
      .map(std => dbModels.dbSampleTestDataFromSampleTestData(ex.id, ex.semanticVersion, collId, std))

    val dbProgUmlClassDiagram = ex.maybeClassDiagramPart
      .map(mcd => dbModels.dbProgUmlClassDiagramFromUmlClassDiagram(ex.id, ex.semanticVersion, collId, mcd)).toList

    val dbUnitTestTestConfigs = ex.unitTestPart.unitTestTestConfigs
      .map(utc => dbModels.dbUnitTestTestConfigFromUnitTestTestConfig(ex.id, ex.semanticVersion, collId, utc))

    val dbUnitTestFiles = ex.unitTestPart.unitTestFiles
      .map(utf => dbModels.dbExerciseFileFromExerciseFile(ex.id, ex.semanticVersion, collId, utf))

    val dbImplementationFiles = ex.implementationPart.files
      .map(ipf => dbModels.dbExerciseFileFromExerciseFile(ex.id, ex.semanticVersion, collId, ipf))

    for {
      samplesSaved <- saveSeq[DbProgSampleSolution](dbSamples, i => db.run(sampleSolutionsTableQuery += i))
      sampleFilesSaved <- saveSeq[DbProgSampleSolutionFile](dbSampleFiles, i => db.run(sampleSolutionFilesTableQuery += i))
      inputTypesSaved <- saveSeq[DbProgInput](dbProgInputs, i => db.run(inputTypesQuery += i))
      sampleTestDataSaved <- saveSeq[DbProgSampleTestData](dbSampleTestData, i => db.run(sampleTestData += i))
      unitTestTestConfigsSaved <- saveSeq[DbUnitTestTestConfig](dbUnitTestTestConfigs, i => db.run(unitTestTestConfigsTQ += i))
      unitTestFilesSaved <- saveSeq[DbExerciseFile](dbUnitTestFiles, i => db.run(progUnitTestFilesTQ += i))
      implementationFilesSaved <- saveSeq[DbExerciseFile](dbImplementationFiles, i => db.run(implementationFilesTQ += i))
      classDiagPartSaved <- saveSeq[DbProgUmlClassDiagram](dbProgUmlClassDiagram, i => db.run(umlClassDiagParts += i))
    } yield samplesSaved && sampleFilesSaved && inputTypesSaved && sampleTestDataSaved && unitTestTestConfigsSaved && unitTestFilesSaved && classDiagPartSaved
  }


  private def futureSampleSolutionFilesForDbSampleSol(dbSampleSol: DbProgSampleSolution): Future[Seq[DbProgSampleSolutionFile]] =
    db.run(
      sampleSolutionFilesTableQuery
        .filter { sf => sf.solutionId === dbSampleSol.id && sf.exId === dbSampleSol.exId && sf.collId === dbSampleSol.collId }
        .result
    )

  override def futureSampleSolutionsForExPart(collId: Int, id: Int, part: ProgExPart): Future[Seq[ProgSampleSolution]] =
    db.run(
      sampleSolutionsTableQuery
        .filter { sample => sample.exerciseId === id && sample.collectionId === collId }
        .result
    ).flatMap { dbSampleSols: Seq[DbProgSampleSolution] =>
      Future.sequence(dbSampleSols.map { dbSampleSol =>
        futureSampleSolutionFilesForDbSampleSol(dbSampleSol).map {
          sampleSolFiles => ProgSolutionDbModels.sampleSolFromDbSampleSol(dbSampleSol, sampleSolFiles)
        }
      })
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

      val dbUserSol = dbUserSolWithFiles._1.copy(id = nextSolId)
      val dbUserSolFiles = dbUserSolWithFiles._2.map(_.copy(solId = nextSolId))

      for {
        solSaved <- db.run(userSolutionsTableQuery += dbUserSol) transform(_ == 1, identity)
        solFilesSaved <- saveSeq[DbProgUserSolutionFile](dbUserSolFiles, i => db.run(userSolutionFilesTableQuery += i))
      } yield solSaved && solFilesSaved
    }

}

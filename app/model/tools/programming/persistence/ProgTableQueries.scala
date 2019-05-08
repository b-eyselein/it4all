package model.tools.programming.persistence

import model.persistence.DbExerciseFile
import model.tools.programming._
import model.tools.uml.UmlClassDiagram
import model.{ExerciseFile, SemanticVersion}

import scala.concurrent.Future

trait ProgTableQueries {
  self: ProgTableDefs =>

  import profile.api._

  private def sampleSolutionForExercise(collId: Int, ex: DbProgExercise): Future[Seq[ProgSampleSolution]] =
    db.run(sampleSolutionsTableQuery.filter { s => s.exerciseId === ex.id && s.collectionId === collId }.result)
      .map(_.map(ProgSolutionDbModels.sampleSolFromDbSampleSol))

  private def inputTypeForExercise(collId: Int, ex: DbProgExercise): Future[Seq[ProgInput]] =
    db.run(inputTypesQuery.filter { it => it.exerciseId === ex.id && it.collectionId === collId }.result)
      .map(_.map(dbModels.progInputFromDbProgInput))

  private def sampleTestDataForExercise(collId: Int, ex: DbProgExercise): Future[Seq[ProgSampleTestData]] =
    db.run(sampleTestData.filter { st => st.exerciseId === ex.id && st.collectionId === collId }.result)
      .map(_.map(dbModels.sampleTestDataFromDbSampleTestData))

  private def unitTestTestConfigsForExercise(collId: Int, ex: DbProgExercise): Future[Seq[UnitTestTestConfig]] =
    db.run(unitTestTestConfigsTQ.filter { utc => utc.exerciseId === ex.id && utc.collectionId === collId }.result)
      .map(_.map(dbModels.unitTestTestConfigFromDbUnitTestTestConfig))

  private def unitTestFilesForExercise(collId: Int, ex: DbProgExercise): Future[Seq[ExerciseFile]] = db.run(
    progUnitTestFilesTQ
      .filter { wf => wf.exerciseId === ex.id && wf.collectionId === collId }
      .result).map(_.map(ProgDbModels.exerciseFileFromDbExerciseFile))

  private def maybeClassDiagramPartForExercise(collId: Int, ex: DbProgExercise): Future[Option[UmlClassDiagram]] =
    db.run(umlClassDiagParts.filter { cdp => cdp.exerciseId === ex.id && cdp.collectionId === collId }.result.headOption)
      .map(_.map(_.classDiagram))

  override def completeExForEx(collId: Int, ex: DbProgExercise): Future[ProgExercise] = for {
    samples <- sampleSolutionForExercise(collId, ex)
    inputTypes <- inputTypeForExercise(collId, ex)
    sampleTestData <- sampleTestDataForExercise(collId, ex)
    unitTestTestConfigs <- unitTestTestConfigsForExercise(collId, ex)
    unitTestFiles <- unitTestFilesForExercise(collId, ex)
    maybeClassDiagram <- maybeClassDiagramPartForExercise(collId, ex)
  } yield dbModels.exerciseFromDbValues(ex, inputTypes, samples, sampleTestData, unitTestTestConfigs, unitTestFiles, maybeClassDiagram)

  override def saveExerciseRest(collId: Int, ex: ProgExercise): Future[Boolean] = {
    val dbSamples = ex.sampleSolutions map (s => ProgSolutionDbModels.dbSampleSolFromSampleSol(ex.id, ex.semanticVersion, collId, s))
    val dbProgInputs = ex.inputTypes map (it => dbModels.dbProgInputFromProgInput(ex.id, ex.semanticVersion, collId, it))
    val dbSampleTestData = ex.sampleTestData map (std => dbModels.dbSampleTestDataFromSampleTestData(ex.id, ex.semanticVersion, collId, std))
    val dbProgUmlClassDiagram = ex.maybeClassDiagramPart.map(mcd => dbModels.dbProgUmlClassDiagramFromUmlClassDiagram(ex.id, ex.semanticVersion, collId, mcd)).toList
    val dbUnitTestTestConfigs = ex.unitTestTestConfigs.map(utc => dbModels.dbUnitTestTestConfigFromUnitTestTestConfig(ex.id, ex.semanticVersion, collId, utc))
    val dbUnitTestFiles = ex.unitTestFiles.map(utf => dbModels.dbExerciseFileFromExerciseFile(ex.id, ex.semanticVersion, collId, utf))

    for {
      samplesSaved <- saveSeq[DbProgSampleSolution](dbSamples, i => db.run(sampleSolutionsTableQuery += i))
      inputTypesSaved <- saveSeq[DbProgInput](dbProgInputs, i => db.run(inputTypesQuery += i))
      sampleTestDataSaved <- saveSeq[DbProgSampleTestData](dbSampleTestData, i => db.run(sampleTestData += i))
      unitTestTestConfigsSaved <- saveSeq[DbUnitTestTestConfig](dbUnitTestTestConfigs, i => db.run(unitTestTestConfigsTQ += i))
      unitTestFilesSaved <- saveSeq[DbExerciseFile](dbUnitTestFiles, i => db.run(progUnitTestFilesTQ += i))
      classDiagPartSaved <- saveSeq[DbProgUmlClassDiagram](dbProgUmlClassDiagram, i => db.run(umlClassDiagParts += i))
    } yield samplesSaved && inputTypesSaved && sampleTestDataSaved && unitTestTestConfigsSaved && unitTestFilesSaved && classDiagPartSaved
  }

  override def futureSampleSolutionsForExPart(collId: Int, id: Int, part: ProgExPart): Future[Seq[ProgSampleSolution]] =
    db.run(sampleSolutionsTableQuery.filter(sample => sample.exerciseId === id && sample.collectionId === collId).result)
      .map(_.map(ProgSolutionDbModels.sampleSolFromDbSampleSol))


  def futureMaybeOldSolution(username: String, collectionId: Int, exerciseId: Int, part: ProgExPart): Future[Option[ProgUserSolution]] = db.run(
    userSolutionsTableQuery
      .filter {
        sol => sol.username === username && sol.collectionId === collectionId && sol.exerciseId === exerciseId && sol.part === part
      }
      .sortBy(_.id.desc) // take last sample sol (with highest id)
      .result
      .headOption
      .map(_ map ProgSolutionDbModels.userSolFromDbUserSol))

  def futureSaveUserSolution(exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, sol: ProgUserSolution): Future[Boolean] =
    nextUserSolutionId(exId, collId, username, sol.part).flatMap { nextSolId =>
      val dbUserSol = ProgSolutionDbModels.dbUserSolFromUserSol(exId, exSemVer, collId, username, sol).copy(id = nextSolId)

      db.run(userSolutionsTableQuery += dbUserSol) transform(_ == 1, identity)
    }


}

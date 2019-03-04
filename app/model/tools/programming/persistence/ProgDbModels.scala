package model.tools.programming.persistence

import model.persistence._
import model.points.Points
import model.tools.programming._
import model.tools.uml.UmlClassDiagram
import model.{Difficulty, ExerciseState, SemanticVersion}
import play.api.libs.json.JsValue

object ProgDbModels extends ADbModels[ProgExercise, DbProgExercise] {

  def dbExerciseFromExercise(collId: Int, ex: ProgExercise): DbProgExercise =
    DbProgExercise(ex.id, ex.semanticVersion, collId, ex.title, ex.author, ex.text, ex.state, ex.folderIdentifier, ex.functionName, ex.outputType, ex.baseData)

  def exerciseFromDbValues(dbProgEx: DbProgExercise, inputTypes: Seq[ProgInput], sampleSolutions: Seq[ProgSampleSolution],
                           sampleTestData: Seq[ProgSampleTestData], maybeClassDiagramPart: Option[UmlClassDiagram]): ProgExercise =
    ProgExercise(
      dbProgEx.id, dbProgEx.semanticVersion, dbProgEx.title, dbProgEx.author, dbProgEx.text, dbProgEx.state,
      dbProgEx.folderIdentifier, dbProgEx.functionname, dbProgEx.outputType, dbProgEx.baseData,
      inputTypes, sampleSolutions, sampleTestData, maybeClassDiagramPart
    )

  // Inputs

  def dbProgInputFromProgInput(exId: Int, exSemVer: SemanticVersion, collId: Int, progInput: ProgInput): DbProgInput =
    DbProgInput(progInput.id, exId, exSemVer, collId, progInput.inputName, progInput.inputType)

  def progInputFromDbProgInput(dbProgInput: DbProgInput): ProgInput =
    ProgInput(dbProgInput.id, dbProgInput.inputName, dbProgInput.inputType)

  // Sample Test Data

  def dbSampleTestDataFromSampleTestData(exId: Int, exSemVer: SemanticVersion, collId: Int, testData: ProgSampleTestData): DbProgSampleTestData =
    DbProgSampleTestData(testData.id, exId, exSemVer, collId, testData.inputAsJson, testData.output)

  def sampleTestDataFromDbSampleTestData(dbTestData: DbProgSampleTestData): ProgSampleTestData =
    ProgSampleTestData(dbTestData.id, dbTestData.inputAsJson, dbTestData.output)

  // User Test Data

  def dbUserTestDataFromUserTestData(exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, testData: ProgUserTestData): DbProgUserTestData =
    DbProgUserTestData(testData.id, exId, exSemVer, collId, username, testData.inputAsJson, testData.output, testData.state)

  def userTestDataFromDbUserTestData(dbTestData: DbProgUserTestData): ProgUserTestData =
    ProgUserTestData(dbTestData.id, dbTestData.inputAsJson, dbTestData.output, dbTestData.state)


  // Uml Class Diagram

  def dbProgUmlClassDiagramFromUmlClassDiagram(exId: Int, exSemVer: SemanticVersion, collId: Int, classDiagram: UmlClassDiagram): DbProgUmlClassDiagram =
    DbProgUmlClassDiagram(exId, exSemVer, collId, classDiagram)

}

object ProgSolutionDbModels extends ASolutionDbModels[ProgSolution, ProgExPart, ProgSampleSolution, DbProgSampleSolution, ProgUserSolution, DbProgUserSolution] {

  def progTestDataToJson(testData: Seq[ProgUserTestData]): JsValue = ???

  def testDataFromJson(jsValue: JsValue): Seq[ProgUserTestData] = ???

  // Sample solutions

  override def dbSampleSolFromSampleSol(exId: Int, exSemVer: SemanticVersion, collId: Int, sample: ProgSampleSolution): DbProgSampleSolution =
    DbProgSampleSolution(sample.id, exId, exSemVer, collId, sample.language, sample.base, sample.sample.implementation)

  override def sampleSolFromDbSampleSol(dbSample: DbProgSampleSolution): ProgSampleSolution =
    ProgSampleSolution(dbSample.id, dbSample.language, dbSample.base, dbSample.sample.implementation)

  // User solutions

  override def dbUserSolFromUserSol(exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, solution: ProgUserSolution): DbProgUserSolution =
    DbProgUserSolution(solution.id, exId, exSemVer, collId, username, solution.part, solution.solution.implementation, progTestDataToJson(solution.solution.testData),
      solution.language, solution.extendedUnitTests, solution.points, solution.maxPoints)

  override def userSolFromDbUserSol(dbSol: DbProgUserSolution): ProgUserSolution =
    ProgUserSolution(dbSol.id, dbSol.part, dbSol.solution, dbSol.language, dbSol.extendedUnitTests, dbSol.points, dbSol.maxPoints)

}

object ProgExerciseReviewDbModels extends AExerciseReviewDbModels[ProgExPart, ProgExerciseReview, DbProgrammingExerciseReview] {

  override def dbReviewFromReview(username: String, collId: Int, exId: Int, part: ProgExPart, review: ProgExerciseReview): DbProgrammingExerciseReview =
    DbProgrammingExerciseReview(username, collId, exId, part, review.difficulty, review.maybeDuration)

  override def reviewFromDbReview(dbReview: DbProgrammingExerciseReview): ProgExerciseReview =
    ProgExerciseReview(dbReview.difficulty, dbReview.maybeDuration)

}

final case class DbProgExercise(id: Int, semanticVersion: SemanticVersion, collectionId: Int, title: String, author: String, text: String, state: ExerciseState,
                                folderIdentifier: String, functionname: String, outputType: ProgDataType, baseData: Option[JsValue]) extends ADbExercise


final case class DbProgSampleSolution(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, language: ProgLanguage, base: String, sampleStr: String)
  extends ADbSampleSol[ProgSolution] {

  override val sample = ProgSolution(sampleStr, testData = Seq[ProgUserTestData](), extendedUnitTests = false, language)

}

final case class DbProgUserSolution(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, part: ProgExPart,
                                    implementation: String, testData: JsValue, language: ProgLanguage, extendedUnitTests: Boolean, points: Points, maxPoints: Points)
  extends ADbUserSol[ProgExPart, ProgSolution] {


  override val solution: ProgSolution = ProgSolution(implementation, ProgSolutionDbModels.testDataFromJson(testData), extendedUnitTests, language)

}


final case class DbProgInput(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, inputName: String, inputType: ProgDataType)


sealed trait DbProgTestData {
  val id         : Int
  val exId       : Int
  val exSemVer   : SemanticVersion
  val collId     : Int
  val inputAsJson: JsValue
  val output     : JsValue
}

final case class DbProgSampleTestData(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, inputAsJson: JsValue, output: JsValue) extends DbProgTestData

final case class DbProgUserTestData(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, inputAsJson: JsValue, output: JsValue, state: ExerciseState) extends DbProgTestData

final case class DbProgUmlClassDiagram(exId: Int, exSemVer: SemanticVersion, collId: Int, classDiagram: UmlClassDiagram)

// Exercise Review


final case class DbProgrammingExerciseReview(username: String, collId: Int, exerciseId: Int, exercisePart: ProgExPart,
                                             difficulty: Difficulty, maybeDuration: Option[Int]) extends DbExerciseReview[ProgExPart]

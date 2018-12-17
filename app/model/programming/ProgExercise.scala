package model.programming

import model._
import model.programming.ProgConsts._
import play.api.libs.json.{JsObject, JsValue, Json}
import play.twirl.api.Html

// Classes for use

final case class ProgCompleteEx(ex: ProgExercise, inputTypes: Seq[ProgInput], sampleSolutions: Seq[ProgSampleSolution],
                                sampleTestData: Seq[SampleTestData], maybeClassDiagramPart: Option[UmlClassDiagPart])
  extends SingleCompleteEx[ProgExercise, ProgExPart] {

  override def preview: Html = // FIXME: move to toolMain!
    views.html.idExercises.programming.progPreview(this)

  val inputCount: Int = inputTypes.size

  override def hasPart(partType: ProgExPart): Boolean = partType match {
    case ProgExParts.Implementation  => true
    case ProgExParts.ActivityDiagram => true
    // TODO: Creation of test data is currently disabled
    case ProgExParts.TestdataCreation => false
  }

  def buildTestDataFileContent(completeTestData: Seq[TestData], extendedUnitTests: Boolean): JsValue = if (extendedUnitTests) ???
  else TestDataJsonFormat.dumpTestDataToJson(this, completeTestData)

}

// Case classes for tables

final case class ProgExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                              folderIdentifier: String, functionname: String, indentLevel: Int, outputType: ProgDataType,
                              baseData: Option[JsValue]) extends Exercise


final case class ProgInput(id: Int, exerciseId: Int, exSemVer: SemanticVersion, inputName: String, inputType: ProgDataType)

final case class ProgSampleSolution(exerciseId: Int, exSemVer: SemanticVersion, language: ProgLanguage, base: String, solution: String)

sealed trait TestData {

  val id         : Int
  val exerciseId : Int
  val inputAsJson: JsValue
  val output     : JsValue

}

final case class SampleTestData(id: Int, exerciseId: Int, exSemVer: SemanticVersion, inputAsJson: JsValue, output: JsValue) extends TestData

final case class CommitedTestData(id: Int, exerciseId: Int, exSemVer: SemanticVersion, inputAsJson: JsValue, output: JsValue, username: String, state: ExerciseState) extends TestData {

  def toJson: JsObject = Json.obj(
    idName -> id,
    exerciseIdName -> exerciseId,
    usernameName -> username,
    outputName -> output,
    statusName -> state.entryName,
    inputsName -> inputAsJson
  )

}

// Solution types

sealed trait ProgSolution {

  val language: ProgLanguage

  def extendedUnitTests: Boolean

  def solution: String

}

final case class ProgStringSolution(solution: String, extendedUnitTests: Boolean, language: ProgLanguage) extends ProgSolution

final case class ProgTestDataSolution(testData: Seq[CommitedTestData], language: ProgLanguage) extends ProgSolution {

  override def solution: String = ???

  override def extendedUnitTests: Boolean = false

}

final case class DBProgSolution(id: Int, username: String, exerciseId: Int, exSemVer: SemanticVersion, part: ProgExPart,
                                solutionStr: String, language: ProgLanguage, extendedUnitTests: Boolean, points: Points, maxPoints: Points) extends DBPartSolution[ProgExPart, ProgSolution] {

  val solution: ProgSolution = part match {
    case ProgExParts.TestdataCreation => ??? // ProgTestDataSolution(???, language)
    case _                            => ProgStringSolution(solutionStr, extendedUnitTests, language)
  }

  def commitedTestData: Seq[CommitedTestData] = solution match {
    case ProgTestDataSolution(td, _) => td
    case _                           => Seq[CommitedTestData]()
  }

}

final case class ProgExerciseReview(username: String, exerciseId: Int, exerciseSemVer: SemanticVersion, exercisePart: ProgExPart,
                                    difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview[ProgExPart]
package model.programming

import model._
import play.api.libs.json.JsValue
import play.twirl.api.Html

// Classes for use

final case class ProgExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                              folderIdentifier: String, functionName: String, outputType: ProgDataType, baseData: Option[JsValue],
                              inputTypes: Seq[ProgInput],
                              sampleSolutions: Seq[ProgSampleSolution],
                              sampleTestData: Seq[SampleTestData],
                              maybeClassDiagramPart: Option[UmlClassDiagPart]) extends Exercise {

  override def baseValues: BaseValues = BaseValues(id, semanticVersion, title, author, text, state)

  // Other methods

  val inputCount: Int = inputTypes.size

  override def preview: Html = // FIXME: move to toolMain!
    views.html.idExercises.programming.progPreview(this)

  def buildTestDataFileContent(completeTestData: Seq[TestData], extendedUnitTests: Boolean): JsValue =
    if (extendedUnitTests) ???
    else TestDataJsonFormat.dumpTestDataToJson(this, completeTestData)

}

// Case classes for tables

final case class ProgInput(id: Int, exerciseId: Int, exSemVer: SemanticVersion, inputName: String, inputType: ProgDataType)

final case class ProgSampleSolution(id: Int, exerciseId: Int, exSemVer: SemanticVersion, language: ProgLanguage, base: String, solutionStr: String)
  extends SampleSolution[ProgSolution] {

  val part = ProgExParts.Implementation

  val sample: ProgSolution = part match {
    //    case ProgExParts.TestdataCreation => ??? // ProgTestDataSolution(???, language)
    case _ => ProgStringSolution(solutionStr, extendedUnitTests = false, language)
  }

}

sealed trait TestData {

  val id         : Int
  val exerciseId : Int
  val inputAsJson: JsValue
  val output     : JsValue

}

final case class SampleTestData(id: Int, exerciseId: Int, exSemVer: SemanticVersion, inputAsJson: JsValue, output: JsValue) extends TestData

final case class CommitedTestData(id: Int, exerciseId: Int, exSemVer: SemanticVersion, inputAsJson: JsValue, output: JsValue, username: String, state: ExerciseState) extends TestData

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
                                solutionStr: String, language: ProgLanguage, extendedUnitTests: Boolean, points: Points, maxPoints: Points) extends UserSolution[ProgExPart, ProgSolution] {

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

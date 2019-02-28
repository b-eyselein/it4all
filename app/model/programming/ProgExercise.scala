package model.programming

import model._
import play.api.libs.json.JsValue
import play.twirl.api.Html

// Classes for use

final case class ProgExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                              folderIdentifier: String, functionName: String, outputType: ProgDataType, baseData: Option[JsValue],
                              inputTypes: Seq[ProgInput],
                              sampleSolutions: Seq[ProgSampleSolution],
                              sampleTestData: Seq[ProgSampleTestData],
                              maybeClassDiagramPart: Option[UmlClassDiagPart]) extends Exercise {

  override def baseValues: BaseValues = BaseValues(id, semanticVersion, title, author, text, state)

  // Other methods

  val inputCount: Int = inputTypes.size

  override def preview: Html = // FIXME: move to toolMain!
    views.html.idExercises.programming.progPreview(this)

  def buildTestDataFileContent(completeTestData: Seq[ProgTestData], extendedUnitTests: Boolean): JsValue =
    if (extendedUnitTests) ???
    else TestDataJsonFormat.dumpTestDataToJson(this, completeTestData)

}

// Case classes for tables

//FIXME: remove exId, exSemVer
final case class ProgInput(id: Int, inputName: String, inputType: ProgDataType)

final case class ProgSampleSolution(id: Int, language: ProgLanguage, base: String, solutionStr: String)
  extends SampleSolution[ProgSolution] {

  val part = ProgExParts.Implementation

  val sample: ProgSolution = part match {
    //    case ProgExParts.TestdataCreation => ??? // ProgTestDataSolution(???, language)
    case _ => ProgStringSolution(solutionStr, extendedUnitTests = false, language)
  }

}

sealed trait ProgTestData {

  val id         : Int
  val inputAsJson: JsValue
  val output     : JsValue

}

final case class ProgSampleTestData(id: Int, inputAsJson: JsValue, output: JsValue) extends ProgTestData

final case class ProgUserTestData(id: Int, inputAsJson: JsValue, output: JsValue, state: ExerciseState) extends ProgTestData

// Solution types

sealed trait ProgSolution {

  val language: ProgLanguage

  def extendedUnitTests: Boolean

  def solution: String

}

final case class ProgStringSolution(solution: String, extendedUnitTests: Boolean, language: ProgLanguage) extends ProgSolution

final case class ProgTestDataSolution(testData: Seq[ProgUserTestData], language: ProgLanguage) extends ProgSolution {

  override def solution: String = ???

  override def extendedUnitTests: Boolean = false

}

final case class ProgUserSolution(id: Int, part: ProgExPart, solution: ProgSolution, language: ProgLanguage, extendedUnitTests: Boolean, points: Points, maxPoints: Points)
  extends UserSolution[ProgExPart, ProgSolution] {


  def commitedTestData: Seq[ProgUserTestData] = solution match {
    case ProgTestDataSolution(td, _) => td
    case _                           => Seq[ProgUserTestData]()
  }

}

final case class ProgExerciseReview(username: String, exerciseId: Int, exerciseSemVer: SemanticVersion, exercisePart: ProgExPart,
                                    difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview[ProgExPart]

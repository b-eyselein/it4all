package model.programming

import model._
import model.programming.ProgConsts._
import model.programming.ProgDataTypes.ProgDataType
import play.api.libs.json.{JsObject, JsValue, Json}
import play.twirl.api.Html

// Classes for use

case class ProgCompleteEx(ex: ProgExercise, inputTypes: Seq[ProgInput], sampleSolutions: Seq[ProgSampleSolution],
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

  def addIndent(solution: String): String = solution split "\n" map (str => " " * (4 * ex.indentLevel) + str) mkString "\n"

}

// Case classes for tables

case class ProgExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                        folderIdentifier: String, base: String, functionname: String, indentLevel: Int,
                        outputType: ProgDataType, baseData: Option[JsValue]) extends Exercise


case class ProgInput(id: Int, exerciseId: Int, exSemVer: SemanticVersion, inputName: String, inputType: ProgDataType)

case class ProgSampleSolution(exerciseId: Int, exSemVer: SemanticVersion, language: ProgLanguage, base: String, solution: String)

sealed trait TestData {

  val id         : Int
  val exerciseId : Int
  val inputAsJson: JsValue
  val output     : String

}

case class SampleTestData(id: Int, exerciseId: Int, exSemVer: SemanticVersion, inputAsJson: JsValue, output: String) extends TestData

case class CommitedTestData(id: Int, exerciseId: Int, exSemVer: SemanticVersion, inputAsJson: JsValue, output: String, username: String, state: ExerciseState) extends TestData {

  def toJson: JsObject = Json.obj(
    idName -> id,
    exerciseIdName -> exerciseId,
    usernameName -> username,
    outputName -> output,
    stateName -> state.entryName,
    inputsName -> inputAsJson
  )

}

// Solution types

sealed trait ProgSolution {

  val language: ProgLanguage

  def solution: String

}

case class ProgStringSolution(solution: String, language: ProgLanguage) extends ProgSolution

case class ProgTestDataSolution(testData: Seq[CommitedTestData], language: ProgLanguage) extends ProgSolution {

  override def solution: String = ???

}

case class DBProgSolution(username: String, exerciseId: Int, exSemVer: SemanticVersion, part: ProgExPart,
                          solutionStr: String, language: ProgLanguage, points: Points, maxPoints: Points)
  extends DBPartSolution[ProgExPart, ProgSolution] {

  val solution: ProgSolution = part match {
    case ProgExParts.TestdataCreation => ??? // ProgTestDataSolution(???, language)
    case _                            => ProgStringSolution(solutionStr, language)
  }

  def commitedTestData: Seq[CommitedTestData] = solution match {
    case ProgTestDataSolution(td, _) => td
    case _                           => Seq.empty
  }

}

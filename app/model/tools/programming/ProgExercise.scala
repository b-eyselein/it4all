package model.tools.programming

import enumeratum.{EnumEntry, PlayEnum}
import model._
import model.points.Points
import model.tools.uml.UmlClassDiagram
import play.api.libs.json.JsValue
import play.twirl.api.Html

import scala.collection.immutable


final case class ProgCollection(id: Int, title: String, author: String, text: String, state: ExerciseState, shortName: String)
  extends ExerciseCollection

final case class ProgExercise(
  id: Int,
  semanticVersion: SemanticVersion,
  title: String,
  author: String,
  text: String,
  state: ExerciseState,
  functionName: String,
  outputType: ProgDataType,
  baseData: Option[JsValue],
  unitTestType: UnitTestType,
  inputTypes: Seq[ProgInput],
  sampleSolutions: Seq[ProgSampleSolution],
  sampleTestData: Seq[ProgSampleTestData],
  unitTestsDescription: String,
  unitTestFiles: Seq[ExerciseFile],
  foldername: String,
  filename: String,
  unitTestTestConfigs: Seq[UnitTestTestConfig],
  maybeClassDiagramPart: Option[UmlClassDiagram]) extends Exercise with FileExercise[ProgExPart] {

  override def baseValues: BaseValues = BaseValues(id, semanticVersion, title, author, text, state)

  // Other methods

  val inputCount: Int = inputTypes.size

  def buildSimpleTestDataFileContent(completeTestData: Seq[ProgTestData]): JsValue =
    ProgJsonProtocols.dumpCompleteTestDataToJson(this, completeTestData)

  override def filesForExercisePart(part: ProgExPart): Seq[ExerciseFile] = part match {
    case ProgExParts.TestCreation => unitTestFiles
    case _                        => Seq(ExerciseFile("solution.py", "", "python", true))
  }

  override def preview: Html = // FIXME: move to toolMain!
    views.html.toolViews.programming.progPreview(this)

}


sealed trait UnitTestType extends EnumEntry

case object UnitTestTypes extends PlayEnum[UnitTestType] {

  override val values: immutable.IndexedSeq[UnitTestType] = findValues


  case object Simplified extends UnitTestType

  case object Normal extends UnitTestType

}

final case class ProgInput(id: Int, inputName: String, inputType: ProgDataType)

final case class ProgSolution(implementation: String, testData: Seq[ProgUserTestData], unitTest: ExerciseFile) {

  def language: ProgLanguage = ProgLanguages.PYTHON_3

}


sealed trait ProgTestData {

  val id    : Int
  val input : JsValue
  val output: JsValue

}

final case class ProgSampleTestData(id: Int, input: JsValue, output: JsValue) extends ProgTestData

final case class ProgUserTestData(id: Int, input: JsValue, output: JsValue, state: ExerciseState) extends ProgTestData

// Solution types

final case class ProgSampleSolution(id: Int, base: String, solutionStr: String, unitTest: ExerciseFile)
  extends SampleSolution[ProgSolution] {

  def language: ProgLanguage = ProgLanguages.PYTHON_3

  val sample: ProgSolution = ProgSolution(implementation = solutionStr, testData = Seq[ProgUserTestData](), unitTest)

}

final case class ProgUserSolution(id: Int, part: ProgExPart, solution: ProgSolution, points: Points, maxPoints: Points)
  extends UserSolution[ProgExPart, ProgSolution] {

  def language: ProgLanguage = ProgLanguages.PYTHON_3

  def commitedTestData: Seq[ProgUserTestData] = solution.testData

}

final case class ProgExerciseReview(difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview

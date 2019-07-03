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
  foldername: String,
  filename: String,

  inputTypes: Seq[ProgInput],
  outputType: ProgDataType,

  baseData: Option[JsValue],

  unitTestPart: UnitTestPart,
  implementationPart: ImplementationPart,

  sampleSolutions: Seq[ProgSampleSolution],
  sampleTestData: Seq[ProgSampleTestData],

  maybeClassDiagramPart: Option[UmlClassDiagram]
) extends Exercise with FileExercise[ProgExPart] {

  val inputCount: Int = inputTypes.size

  def buildSimpleTestDataFileContent(completeTestData: Seq[ProgTestData]): JsValue =
    ProgJsonProtocols.dumpCompleteTestDataToJson(this, completeTestData)

  override def filesForExercisePart(part: ProgExPart): LoadExerciseFilesMessage = part match {
    case ProgExParts.TestCreation    => LoadExerciseFilesMessage(unitTestPart.unitTestFiles, Some("test.py"))
    case ProgExParts.Implementation  => LoadExerciseFilesMessage(implementationPart.files, Some(s"$filename.py"))
    case ProgExParts.ActivityDiagram => ???
  }

  override def preview: Html = // FIXME: move to toolMain!
    views.html.toolViews.programming.progPreview(this)

}


final case class UnitTestPart(
  unitTestType: UnitTestType,
  unitTestsDescription: String,
  unitTestFiles: Seq[ExerciseFile],
  unitTestTestConfigs: Seq[UnitTestTestConfig],
)

final case class ImplementationPart(base: String, files: Seq[ExerciseFile])


sealed trait UnitTestType extends EnumEntry

case object UnitTestTypes extends PlayEnum[UnitTestType] {

  override val values: immutable.IndexedSeq[UnitTestType] = findValues


  case object Simplified extends UnitTestType

  case object Normal extends UnitTestType

}

final case class ProgInput(id: Int, inputName: String, inputType: ProgDataType)

final case class ProgSolution(files: Seq[ExerciseFile], testData: Seq[ProgUserTestData]) {

  def language: ProgLanguage = ProgLanguages.PYTHON_3

  def unitTest: ExerciseFile = files.find(_.name == "test.py").getOrElse(???)

}


sealed trait ProgTestData {

  val id    : Int
  val input : JsValue
  val output: JsValue

}

final case class ProgSampleTestData(id: Int, input: JsValue, output: JsValue) extends ProgTestData

final case class ProgUserTestData(id: Int, input: JsValue, output: JsValue, state: ExerciseState) extends ProgTestData

// Solution types

final case class ProgSampleSolution(id: Int, sample: ProgSolution)
  extends SampleSolution[ProgSolution] {

  def language: ProgLanguage = ProgLanguages.PYTHON_3

}

final case class ProgUserSolution(id: Int, part: ProgExPart, solution: ProgSolution, points: Points, maxPoints: Points)
  extends UserSolution[ProgExPart, ProgSolution] {

  def language: ProgLanguage = ProgLanguages.PYTHON_3

  def commitedTestData: Seq[ProgUserTestData] = solution.testData

}

final case class ProgExerciseReview(difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview

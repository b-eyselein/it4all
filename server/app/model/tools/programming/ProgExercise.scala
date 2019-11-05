package model.tools.programming

import model._
import model.points.Points
import model.tools.uml.UmlClassDiagram
import play.api.libs.json.JsValue
import play.twirl.api.Html


final case class ProgExercise(
  id: Int, collId: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
  functionName: String, foldername: String, filename: String,
  inputTypes: Seq[ProgInput], outputType: ProgDataType,

  baseData: Option[JsValue],

  unitTestPart: UnitTestPart,
  implementationPart: ImplementationPart,

  sampleSolutions: Seq[ProgSampleSolution],
  sampleTestData: Seq[ProgSampleTestData],

  maybeClassDiagramPart: Option[UmlClassDiagram]
) extends Exercise with FileExercise[ProgExPart] {

  val inputCount: Int = inputTypes.size

  def buildSimpleTestDataFileContent(completeTestData: Seq[ProgTestData]): JsValue =
    ProgrammingJsonProtocols.dumpCompleteTestDataToJson(this, completeTestData)

  override def filesForExercisePart(part: ProgExPart): LoadExerciseFilesMessage = part match {
    case ProgExParts.TestCreation    => LoadExerciseFilesMessage(unitTestPart.unitTestFiles, Some(unitTestPart.testFileName))
    case ProgExParts.Implementation  => LoadExerciseFilesMessage(implementationPart.files, Some(implementationPart.implFileName))
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
  testFileName: String,
  sampleSolFileNames: Seq[String]
)

final case class ImplementationPart(base: String, files: Seq[ExerciseFile], implFileName: String, sampleSolFileNames: Seq[String])


final case class ProgInput(id: Int, inputName: String, inputType: ProgDataType)

final case class ProgSolution(files: Seq[ExerciseFile], testData: Seq[ProgUserTestData]) {

  @deprecated(since = "1.0.0")
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

final case class ProgSampleSolution(id: Int, sample: ProgSolution) extends SampleSolution[ProgSolution]


final case class ProgUserSolution(id: Int, part: ProgExPart, solution: ProgSolution, points: Points, maxPoints: Points)
  extends UserSolution[ProgExPart, ProgSolution] {

  def commitedTestData: Seq[ProgUserTestData] = solution.testData

}

final case class ProgExerciseReview(difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview

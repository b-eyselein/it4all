package model.tools.collectionTools.programming

import model._
import model.core.LongText
import model.points.Points
import model.tools.collectionTools.{Exercise, ExerciseFile, LoadExerciseFilesMessage}
import model.tools.collectionTools.uml.UmlClassDiagram
import play.api.libs.json.JsValue


final case class ProgExercise(
  id: Int, collectionId: Int, toolId: String = ProgConsts.toolId, semanticVersion: SemanticVersion,
  title: String, author: String, text: LongText, state: ExerciseState,
  functionName: String, foldername: String, filename: String,
  inputTypes: Seq[ProgInput], outputType: ProgDataType,

  baseData: Option[JsValue],

  unitTestPart: UnitTestPart,
  implementationPart: ImplementationPart,

  sampleSolutions: Seq[ProgSampleSolution],
  sampleTestData: Seq[ProgSampleTestData],

  override val tags: Seq[ProgrammingExerciseTag],

  maybeClassDiagramPart: Option[UmlClassDiagram]
) extends Exercise {

  override protected type SolutionType = ProgSolution

  override protected type SampleSolutionType = ProgSampleSolution


  //  def inputCount: Int = inputTypes.size

  def buildSimpleTestDataFileContent(completeTestData: Seq[ProgTestData]): JsValue =
    ProgrammingToolJsonProtocol.dumpCompleteTestDataToJson(this, completeTestData)

  def filesForExercisePart(part: ProgExPart): LoadExerciseFilesMessage = part match {
    case ProgExParts.TestCreation    => LoadExerciseFilesMessage(unitTestPart.unitTestFiles, Some(unitTestPart.testFileName))
    case ProgExParts.Implementation  => LoadExerciseFilesMessage(implementationPart.files, Some(implementationPart.implFileName))
    case ProgExParts.ActivityDiagram => ???
  }

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

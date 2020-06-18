package model.tools.programming

import model.{ExerciseContent, ExerciseFile, SampleSolution}
import play.api.libs.json.JsValue

final case class ProgrammingExerciseContent(
  functionName: String,
  foldername: String,
  filename: String,
  inputTypes: Seq[ProgInput],
  outputType: ProgDataType,
  unitTestPart: UnitTestPart,
  implementationPart: ImplementationPart,
  sampleTestData: Seq[ProgTestData] = Seq.empty,
  override val sampleSolutions: Seq[SampleSolution[ProgSolution]]
) extends ExerciseContent {

  override protected type S = ProgSolution

  def buildSimpleTestDataFileContent(completeTestData: Seq[ProgTestData]): JsValue =
    ProgrammingToolJsonProtocol.dumpCompleteTestDataToJson(completeTestData)

  def parts: Seq[ProgExPart] =
    if (unitTestPart.unitTestType == UnitTestType.Normal) {
      Seq(ProgExPart.Implementation, ProgExPart.TestCreation)
    } else {
      Seq(ProgExPart.Implementation)
    }

}

final case class UnitTestPart(
  unitTestType: UnitTestType,
  unitTestsDescription: String,
  unitTestFiles: Seq[ExerciseFile],
  unitTestTestConfigs: Seq[UnitTestTestConfig],
  testFileName: String,
  sampleSolFileNames: Seq[String],
  simplifiedTestMainFile: Option[ExerciseFile] = None
)

final case class UnitTestTestConfig(id: Int, shouldFail: Boolean, description: String, file: ExerciseFile)

final case class ImplementationPart(
  base: String,
  files: Seq[ExerciseFile],
  implFileName: String,
  sampleSolFileNames: Seq[String]
)

final case class ProgInput(id: Int, inputName: String, inputType: ProgDataType)

final case class ProgSolution(files: Seq[ExerciseFile])

final case class ProgTestData(id: Int, input: JsValue, output: JsValue)

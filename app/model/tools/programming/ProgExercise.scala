package model.tools.programming

import model.tools._
import model.tools.uml.UmlClassDiagram
import play.api.libs.json.JsValue

final case class ProgrammingExerciseContent(
  functionName: String,
  foldername: String,
  filename: String,
  inputTypes: Seq[ProgInput],
  outputType: ProgDataType,
  baseData: Option[JsValue],
  unitTestPart: UnitTestPart,
  implementationPart: ImplementationPart,
  sampleTestData: Seq[ProgTestData],
  maybeClassDiagramPart: Option[UmlClassDiagram],
  sampleSolutions: Seq[SampleSolution[ProgSolution]]
) extends ExerciseContent[ProgSolution] {

  def buildSimpleTestDataFileContent(completeTestData: Seq[ProgTestData]): JsValue =
    ProgrammingToolJsonProtocol.dumpCompleteTestDataToJson(this.baseData, completeTestData)

}

final case class UnitTestPart(
  unitTestType: UnitTestType,
  unitTestsDescription: String,
  unitTestFiles: Seq[ExerciseFile],
  unitTestTestConfigs: Seq[UnitTestTestConfig],
  simplifiedTestMainFile: Option[ExerciseFile],
  testFileName: String,
  sampleSolFileNames: Seq[String]
)

final case class UnitTestTestConfig(id: Int, shouldFail: Boolean, description: String, file: ExerciseFile)

final case class ImplementationPart(
  base: String,
  files: Seq[ExerciseFile],
  implFileName: String,
  sampleSolFileNames: Seq[String]
)

final case class ProgInput(id: Int, inputName: String, inputType: ProgDataType)

final case class ProgSolution(files: Seq[ExerciseFile], testData: Seq[ProgTestData])

final case class ProgTestData(id: Int, input: JsValue, output: JsValue)

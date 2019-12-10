package model.tools.collectionTools.programming

import model.tools.collectionTools.uml.UmlClassDiagram
import model.tools.collectionTools.{ExerciseContent, ExerciseFile, SampleSolution}
import play.api.libs.json.JsValue

final case class ProgExerciseContent(
  functionName: String, foldername: String, filename: String,
  inputTypes: Seq[ProgInput], outputType: ProgDataType,

  baseData: Option[JsValue],

  unitTestPart: UnitTestPart,
  implementationPart: ImplementationPart,

  sampleSolutions: Seq[SampleSolution[ProgSolution]],
  sampleTestData: Seq[ProgTestData],

  maybeClassDiagramPart: Option[UmlClassDiagram]
) extends ExerciseContent {

  override type SolType = ProgSolution

  def buildSimpleTestDataFileContent(completeTestData: Seq[ProgTestData]): JsValue =
    ProgrammingToolJsonProtocol.dumpCompleteTestDataToJson(this, completeTestData)

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

final case class ProgSolution(files: Seq[ExerciseFile], testData: Seq[ProgTestData]) {

  @deprecated(since = "1.0.0")
  def unitTest: ExerciseFile = files.find(_.name == "test.py").getOrElse(???)

}

final case class ProgTestData(id: Int, input: JsValue, output: JsValue)

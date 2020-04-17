package model.tools.programming

import model.tools._
import model.tools.uml.UmlClassDiagram
import play.api.libs.json.JsValue

final case class ProgrammingExercise(
  id: Int,
  collectionId: Int,
  toolId: String,
  title: String,
  authors: Seq[String],
  text: String,
  topics: Seq[Topic],
  difficulty: Int,
  sampleSolutions: Seq[SampleSolution[ProgSolution]],
  content: ProgExerciseContent
) extends Exercise[ProgSolution, ProgExerciseContent]

final case class ProgExerciseContent(
  functionName: String,
  foldername: String,
  filename: String,
  inputTypes: Seq[ProgInput],
  outputType: ProgDataType,
  baseData: Option[JsValue],
  unitTestPart: UnitTestPart,
  implementationPart: ImplementationPart,
  sampleTestData: Seq[ProgTestData],
  maybeClassDiagramPart: Option[UmlClassDiagram]
) {

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

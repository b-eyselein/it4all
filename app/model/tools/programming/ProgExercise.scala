package model.tools.programming

import model.{ExerciseContent, ExerciseFile, SampleSolution}
import play.api.libs.json.JsValue

final case class ProgrammingExerciseContent(
  filename: String,
  unitTestPart: UnitTestPart,
  implementationPart: ImplementationPart,
  override val sampleSolutions: Seq[SampleSolution[ProgSolution]]
) extends ExerciseContent {

  override protected type S = ProgSolution

  def parts: Seq[ProgExPart] =
    unitTestPart match {
      case _: SimplifiedUnitTestPart => Seq(ProgExPart.Implementation)
      case _                         => Seq(ProgExPart.TestCreation, ProgExPart.Implementation)
    }

}

sealed trait UnitTestPart

final case class SimplifiedUnitTestPart(
  simplifiedTestMainFile: ExerciseFile,
  sampleTestData: Seq[ProgTestData]
) extends UnitTestPart

final case class NormalUnitTestPart(
  unitTestsDescription: String,
  unitTestFiles: Seq[ExerciseFile],
  unitTestTestConfigs: Seq[UnitTestTestConfig],
  testFileName: String,
  folderName: String,
  sampleSolFileNames: Seq[String],
  simplifiedTestMainFile: Option[ExerciseFile] = None
) extends UnitTestPart

final case class UnitTestTestConfig(id: Int, shouldFail: Boolean, description: String, file: ExerciseFile)

final case class ImplementationPart(
  base: String,
  files: Seq[ExerciseFile],
  implFileName: String,
  sampleSolFileNames: Seq[String]
)

final case class ProgSolution(files: Seq[ExerciseFile])

final case class ProgTestData(id: Int, input: JsValue, output: JsValue)

package model.tools.programming

import model.{ExerciseContent, ExerciseFile, FilesSolution}
import play.api.libs.json.JsValue

final case class ProgrammingExerciseContent(
  filename: String,
  unitTestPart: UnitTestPart,
  implementationPart: ImplementationPart,
  override val sampleSolutions: Seq[FilesSolution]
) extends ExerciseContent {

  override protected type S = FilesSolution

  def parts: Seq[ProgExPart] =
    unitTestPart match {
      case _: SimplifiedUnitTestPart => Seq(ProgExPart.Implementation)
      case _                         => Seq(ProgExPart.TestCreation, ProgExPart.Implementation)
    }

}

sealed trait UnitTestPart

@deprecated
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
  sampleSolFileNames: Seq[String]
) extends UnitTestPart

final case class UnitTestTestConfig(
  id: Int,
  description: String,
  file: ExerciseFile,
  shouldFail: Boolean = true
)

final case class ImplementationPart(
  base: String,
  files: Seq[ExerciseFile],
  implFileName: String,
  sampleSolFileNames: Seq[String]
)

final case class ProgTestData(id: Int, input: JsValue, output: JsValue)

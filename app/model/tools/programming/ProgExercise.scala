package model.tools.programming

import model.{ExerciseFile, FileExerciseContent, FilesSolution}
import play.api.libs.json.JsValue

final case class ProgrammingExerciseContent(
  filename: String,
  unitTestPart: UnitTestPart,
  implementationPart: ImplementationPart,
  override val sampleSolutions: Seq[FilesSolution]
) extends FileExerciseContent {

  def parts: Seq[ProgExPart] =
    unitTestPart match {
      case _: SimplifiedUnitTestPart => Seq(ProgExPart.Implementation)
      case _                         => Seq(ProgExPart.TestCreation, ProgExPart.Implementation)
    }

}

sealed trait ProgrammingExerciseContentPart {

  // FIXME: use?
  def sampleSolFileNames: Seq[String]

}

sealed trait UnitTestPart extends ProgrammingExerciseContentPart

@deprecated
final case class SimplifiedUnitTestPart(
  simplifiedTestMainFile: ExerciseFile,
  sampleTestData: Seq[ProgTestData]
) extends UnitTestPart {

  override def sampleSolFileNames: Seq[String] = ???

}

final case class NormalUnitTestPart(
  unitTestsDescription: String,
  unitTestFiles: Seq[ExerciseFile],
  unitTestTestConfigs: Seq[UnitTestTestConfig],
  testFileName: String,
  folderName: String
) extends UnitTestPart {

  override def sampleSolFileNames: Seq[String] = Seq(testFileName)

}

final case class UnitTestTestConfig(
  id: Int,
  description: String,
  file: ExerciseFile,
  shouldFail: Boolean = true
)

final case class ImplementationPart(
  files: Seq[ExerciseFile],
  implFileName: String
) extends ProgrammingExerciseContentPart {

  override def sampleSolFileNames: Seq[String] = Seq(implFileName)

}

final case class ProgTestData(id: Int, input: JsValue, output: JsValue)

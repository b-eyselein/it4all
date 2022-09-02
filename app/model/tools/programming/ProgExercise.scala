package model.tools.programming

import model.{ExerciseContentWithParts, ExerciseFile, FileExerciseContent, FilesSolution}

final case class ProgrammingExerciseContent(
  filename: String,
  unitTestPart: UnitTestPart,
  implementationPart: ImplementationPart,
  override val sampleSolutions: Seq[FilesSolution]
) extends FileExerciseContent
    with ExerciseContentWithParts {

  override def parts: Seq[ProgExPart] = ProgExPart.values

}

sealed trait ProgrammingExerciseContentPart {

  // FIXME: use?
  def sampleSolFileNames: Seq[String]

}

final case class UnitTestPart(
  unitTestsDescription: String,
  unitTestFiles: Seq[ExerciseFile],
  unitTestTestConfigs: Seq[UnitTestTestConfig],
  testFileName: String,
  folderName: String
) extends ProgrammingExerciseContentPart {

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

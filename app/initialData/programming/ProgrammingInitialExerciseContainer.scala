package initialData.programming

import better.files.File
import initialData.{FileLoadConfig, InitialFilesExerciseContainer}
import model.tools.programming.{ImplementationPart, UnitTestTestConfig}
import model.{ExerciseFile, PathExerciseFile}

abstract class ProgrammingInitialExerciseContainer(collectionId: Int, exerciseId: Int, protected val exerciseBaseName: String)
    extends InitialFilesExerciseContainer("programming", collectionId, exerciseId) {

  protected val implFileName     = s"$exerciseBaseName.py"
  protected val implDeclFileName = s"${exerciseBaseName}_declaration.py"
  protected val testFileName     = s"test_$exerciseBaseName.py"
  protected val testDeclFileName = s"test_${exerciseBaseName}_declaration.py"

  protected val unitTestSolsDir: File = exResPath / "unit_test_sols"

  protected def unitTestTestConfig(id: Int, description: String, shouldFail: Boolean = true): UnitTestTestConfig = {
    val file = PathExerciseFile(s"${exerciseBaseName}_$id.py", /*fileType,*/ unitTestSolsDir, editable = false)

    UnitTestTestConfig(id, description, file, shouldFail)
  }

  protected def unitTestFiles: Seq[ExerciseFile] = loadFilesFromFolder(
    exResPath,
    Seq(
      FileLoadConfig(implFileName, realFilename = Some(implDeclFileName)),
      FileLoadConfig(testFileName, editable = true, Some(testDeclFileName))
    )
  )

  protected def defaultImplementationPart: ImplementationPart = ImplementationPart(
    files = loadFilesFromFolder(
      exResPath,
      Seq(
        FileLoadConfig(testFileName),
        FileLoadConfig(implFileName, editable = true, Some(implDeclFileName))
      )
    ),
    implFileName = implFileName
  )

  protected def defaultSampleSolutionFiles: Seq[ExerciseFile] = loadFilesFromFolder(
    exResPath,
    Seq(
      FileLoadConfig(s"$exerciseBaseName.py"),
      FileLoadConfig(s"test_$exerciseBaseName.py")
    )
  )

}

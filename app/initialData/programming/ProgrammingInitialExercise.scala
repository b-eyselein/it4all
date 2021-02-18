package initialData.programming

import better.files.File
import initialData.InitialData.loadTextFromFile
import initialData.{FileLoadConfig, InitialFilesExercise}
import model.ExerciseFile
import model.tools.programming.UnitTestTestConfig

abstract class ProgrammingInitialExercise(collectionId: Int, exerciseId: Int, protected val exerciseBaseName: String)
    extends InitialFilesExercise("programming", collectionId, exerciseId) {

  protected val fileType = "python"

  protected val unitTestSolsDir: File = exResPath / "unit_test_sols"

  protected def unitTestTestConfig(id: Int, description: String, shouldFail: Boolean = true): UnitTestTestConfig = {
    val fileName = s"${exerciseBaseName}_$id.py"

    val file = ExerciseFile(fileName, fileType, loadTextFromFile(unitTestSolsDir / fileName), editable = false)

    UnitTestTestConfig(id, description, file, shouldFail)
  }

  protected def unitTestFiles: Seq[ExerciseFile] = loadFilesFromFolder(
    exResPath,
    Seq(
      FileLoadConfig(
        s"$exerciseBaseName.py",
        fileType,
        maybeOtherFileName = Some(s"${exerciseBaseName}_declaration.py")
      ),
      FileLoadConfig(
        s"test_$exerciseBaseName.py",
        fileType,
        editable = true,
        Some(s"test_${exerciseBaseName}_declaration.py")
      )
    )
  )

  protected val sampleSolutionFiles: Seq[ExerciseFile] = loadFilesFromFolder(
    exResPath,
    Seq(
      FileLoadConfig(s"$exerciseBaseName.py", fileType),
      FileLoadConfig(s"test_$exerciseBaseName.py", fileType)
    )
  )

}

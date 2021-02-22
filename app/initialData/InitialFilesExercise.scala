package initialData

import better.files.File
import initialData.InitialData.exerciseResourcesPath
import model.{ExerciseFile, PathExerciseFile}

final case class FileLoadConfig(
  name: String,
  fileType: String,
  editable: Boolean = false,
  maybeOtherFileName: Option[String] = None
)

abstract class InitialFilesExercise(
  protected val toolId: String,
  protected val collectionId: Int,
  protected val exerciseId: Int
) {

  protected val exResPath: File = exerciseResourcesPath(toolId, collectionId, exerciseId)

  protected def loadFilesFromFolder(
    directory: File,
    fileLoadConfigs: Seq[FileLoadConfig]
  ): Seq[ExerciseFile] = fileLoadConfigs.map { case FileLoadConfig(name, fileType, editable, maybeOtherFileName) =>
    val fileName = maybeOtherFileName.getOrElse(name)

    PathExerciseFile(fileName, fileType, directory, editable)
  }

}

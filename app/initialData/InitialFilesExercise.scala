package initialData

import better.files.File
import model.{ExerciseFile, PathExerciseFile}

final case class FileLoadConfig(
  name: String,
  fileType: String,
  editable: Boolean = false,
  maybeOtherFileName: Option[String] = None
)

abstract class InitialFilesExercise(toolId: String, collectionId: Int, exerciseId: Int) extends InitialExercise(toolId, collectionId, exerciseId) {

  protected def loadFilesFromFolder(directory: File, fileLoadConfigs: Seq[FileLoadConfig]): Seq[ExerciseFile] = fileLoadConfigs.map {
    case FileLoadConfig(name, fileType, editable, maybeOtherFileName) =>
      PathExerciseFile(maybeOtherFileName.getOrElse(name), fileType, directory, editable)
  }

}

package initialData

import better.files.File
import model.{ExerciseFile, PathExerciseFile}

final case class FileLoadConfig(
  name: String,
  editable: Boolean = false,
  realFilename: Option[String] = None
)

abstract class InitialFilesExerciseContainer(toolId: String, collectionId: Int, exerciseId: Int)
    extends InitialExerciseContainer(toolId, collectionId, exerciseId) {

  protected def loadFilesFromFolder(directory: File, fileLoadConfigs: Seq[FileLoadConfig]): Seq[ExerciseFile] = fileLoadConfigs.map {
    case FileLoadConfig(name, editable, maybeOtherFileName) => PathExerciseFile(name, directory, editable, maybeOtherFileName)
  }

}

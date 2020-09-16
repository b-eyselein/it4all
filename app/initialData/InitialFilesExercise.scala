package initialData

import better.files.File
import initialData.InitialData.{exerciseResourcesPath, loadTextFromFile}
import model.ExerciseFile

final case class FileLoadConfig(
  name: String,
  fileType: String,
  editable: Boolean = false,
  maybeOtherFileName: Option[String] = None
) {

  def fileName: String = maybeOtherFileName.getOrElse(name)

}

abstract class InitialFilesExercise(
  protected val toolId: String,
  protected val collectionId: Int,
  protected val exerciseId: Int
) {

  protected val exResPath: File = exerciseResourcesPath(toolId, collectionId, exerciseId)

  protected def loadFilesFromFolder(folder: File, fileLoadConfigs: Seq[FileLoadConfig]): Seq[ExerciseFile] =
    fileLoadConfigs.map { fileLoadConfig =>
      ExerciseFile(
        name = fileLoadConfig.name,
        fileType = fileLoadConfig.fileType,
        editable = fileLoadConfig.editable,
        content = loadTextFromFile(folder / fileLoadConfig.fileName)
      )
    }

}

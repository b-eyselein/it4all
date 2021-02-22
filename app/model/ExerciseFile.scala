package model

import better.files._

sealed trait ExerciseFile {
  val name: String
  val fileType: String
  val editable: Boolean

  def content: String

  def writeOrCopyToDirectory(directory: File): File
}

final case class ContentExerciseFile(
  name: String,
  fileType: String,
  content: String,
  editable: Boolean
) extends ExerciseFile {

  def writeOrCopyToDirectory(directory: File): File = {
    val targetPath = directory / name

    targetPath
      .createIfNotExists(createParents = true)
      .write(content)
  }

}

final case class PathExerciseFile(
  name: String,
  fileType: String,
  directory: File,
  editable: Boolean
) extends ExerciseFile {

  def file: File = directory / name

  override def content: String = file.contentAsString

  def writeOrCopyToDirectory(directory: File): File = file.copyTo(directory / name, overwrite = true)

}

// FilesSolution

sealed trait IFilesSolution {

  val files: Seq[ExerciseFile]

}

final case class FilesSolutionInput(
  files: Seq[ContentExerciseFile]
) extends IFilesSolution

final case class FilesSolution(
  files: Seq[ExerciseFile]
) extends IFilesSolution

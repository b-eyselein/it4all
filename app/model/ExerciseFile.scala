package model

import better.files._

final case class ExerciseFile(
  name: String,
  fileType: String,
  content: String,
  editable: Boolean
) {

  def writeOrCopyToDirectory(directory: File): File = {
    val targetPath = directory / name

    targetPath
      .createIfNotExists(createParents = true)
      .write(content)
  }

}

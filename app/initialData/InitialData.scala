package initialData

import better.files.File

trait InitialData {

  protected val toolId: String

}

object InitialData {

  def exerciseResourcesPath(toolId: String, collectionId: Int, exerciseId: Int): File =
    File.currentWorkingDirectory / "conf" / "resources" / toolId / s"coll_$collectionId" / s"ex_$exerciseId"

  def ex_resources_path(toolId: String, collectionId: Int, exerciseId: Int): File =
    exerciseResourcesPath(toolId, collectionId, exerciseId)

  def load_text_from_file(file: File): String = file.contentAsString

}

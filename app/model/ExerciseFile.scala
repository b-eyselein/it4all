package model

import play.api.libs.functional.syntax._
import play.api.libs.json.{Writes, __}
import better.files._


final case class ExerciseFile(path: String, resourcePath: String, fileType: String, editable: Boolean)

object ExerciseFileJsonProtocol {

  private val resourcesBasePath = File("conf") / "resources"

  private val unapplyExerciseFileForWrite: ExerciseFile => (String, String, String, Boolean) = {
    case ExerciseFile(path, resourcePath, fileType, editable) =>
      val file = resourcesBasePath / resourcePath

      println(file.path.toAbsolutePath + " :: " + file.exists)

      (path, file.contentAsString, fileType, editable)
  }

  val exerciseFileWrites: Writes[ExerciseFile] = (
    (__ \ "path").write[String] and
      (__ \ "origContent").write[String] and
      (__ \ "fileType").write[String] and
      (__ \ "editable").write[Boolean]
    ) (unapplyExerciseFileForWrite)

}

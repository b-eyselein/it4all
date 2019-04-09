package model

import better.files._
import model.tools.web.WebConsts._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads.traversableReads
import play.api.libs.json.{Format, Reads, Writes, __}


final case class ExerciseFileWorkspace(fileNum: Int, files: Seq[ExerciseFile])

final case class ExerciseFile(name: String, content: String, fileType: String, editable: Boolean)


object ExerciseFileJsonProtocol {

  val resourcesBasePath = File("conf") / "resources"

  private val exerciseFileWrites: Writes[ExerciseFile] = (
    (__ \ pathName).write[String] and
      (__ \ contentName).write[String] and
      (__ \ "fileType").write[String] and
      (__ \ "editable").write[Boolean]
    ) (unlift(ExerciseFile.unapply))

  private implicit val exerciseFileReads: Reads[ExerciseFile] = (
    (__ \ pathName).read[String] and
      (__ \ contentName).read[String] and
      (__ \ "fileType").read[String] and
      (__ \ "editable").read[Boolean]
    ) (ExerciseFile.apply _)

  val exerciseFileJsonFormat: Format[ExerciseFile] = Format[ExerciseFile](exerciseFileReads, exerciseFileWrites)

  val webSolutionJsonReads: Reads[ExerciseFileWorkspace] = (
    (__ \ "filesNum").read[Int] and
      (__ \ filesName).read[Seq[ExerciseFile]]
    ) (ExerciseFileWorkspace.apply _)

}

package model.ideTest

import play.api.libs.json._
import play.api.libs.functional.syntax._
import model.core.CoreConsts._

object IdeFileJsonProtocol {

  private val ideFileReads: Reads[IdeFile] = (
    (__ \ nameName).read[String] and
      (__ \ "orig_content").read[String] and
      (__ \ "filetype").read[String]
    ) (IdeFile.apply _)

  private val ideFileWrites: Writes[IdeFile] = (
    (__ \ nameName).write[String] and
      (__ \ "orig_content").write[String] and
      (__ \ "filetype").write[String]
    ) (unlift(IdeFile.unapply))

  implicit val ideFileFormat: Format[IdeFile] = Format(ideFileReads, ideFileWrites)

  val ideWorkspaceReads: Reads[IdeWorkspace] = (
    (__ \ "files").read[Seq[IdeFile]] and
      (__ \ "filesNum").read[Int]
    ) (IdeWorkspace.apply _)

}

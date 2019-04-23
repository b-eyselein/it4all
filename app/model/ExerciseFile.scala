package model

import play.api.libs.json.{Format, Json}


final case class ExerciseFileWorkspace(filesNum: Int, files: Seq[ExerciseFile])

final case class ExerciseFile(name: String, content: String, fileType: String, editable: Boolean)


object ExerciseFileJsonProtocol {

  implicit val exerciseFileFormat: Format[ExerciseFile] = Json.format[ExerciseFile]

  val webSolutionJsonReads: Format[ExerciseFileWorkspace] = Json.format[ExerciseFileWorkspace]

}

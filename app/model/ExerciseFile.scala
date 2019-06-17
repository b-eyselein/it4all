package model

import play.api.libs.json.{Format, Json}


final case class LoadExerciseFilesMessage(files: Seq[ExerciseFile], activeFileName: Option[String])


final case class ExerciseFileWorkspace(filesNum: Int, files: Seq[ExerciseFile])

final case class ExerciseFile(name: String, content: String, fileType: String, editable: Boolean)


object ExerciseFileJsonProtocol {

  val exerciseFileFormat: Format[ExerciseFile] = Json.format[ExerciseFile]

  val exerciseFileWorkspaceReads: Format[ExerciseFileWorkspace] = {
    implicit val exFileFormat: Format[ExerciseFile] = exerciseFileFormat

    Json.format[ExerciseFileWorkspace]
  }

  val loadExerciseFilesMessageFormat: Format[LoadExerciseFilesMessage] = {
    implicit val exFileFormat: Format[ExerciseFile] = exerciseFileFormat

    Json.format[LoadExerciseFilesMessage]
  }

}

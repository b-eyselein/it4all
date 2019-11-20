package model

import better.files.File
import play.api.libs.json.{Format, Json}


final case class LoadExerciseFilesMessage(files: Seq[ExerciseFile], activeFileName: Option[String])


final case class ExerciseFileWorkspace(filesNum: Int, files: Seq[ExerciseFile])

final case class ExerciseFile(name: String, resourcePath: String, fileType: String, editable: Boolean) {

  import ExerciseFileJsonProtocol.baseResourcesPath

  def content: String = (baseResourcesPath / resourcePath).contentAsString

}


object ExerciseFileJsonProtocol {

  val baseResourcesPath: File = File.currentWorkingDirectory / "conf" / "resources"


  val exerciseFileFormat: Format[ExerciseFile] = Json.format[ExerciseFile]

  val exerciseFileWorkspaceReads: Format[ExerciseFileWorkspace] = {
    implicit val exFileFormat: Format[ExerciseFile] = exerciseFileFormat

    Json.format[ExerciseFileWorkspace]
  }

  //  val loadExerciseFilesMessageFormat: Format[LoadExerciseFilesMessage] = {
  //    implicit val exFileFormat: Format[ExerciseFile] = exerciseFileFormat
  //
  //    Json.format[LoadExerciseFilesMessage]
  //  }

}

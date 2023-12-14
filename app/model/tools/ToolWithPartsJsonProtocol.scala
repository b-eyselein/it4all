package model.tools

import better.files.File
import model._
import play.api.libs.json._

import scala.annotation.unused

trait ToolJsonProtocol[SolutionInputFormat, C <: ExerciseContent] {
  val solutionInputFormat: Format[SolutionInputFormat]
  val exerciseContentFormat: OFormat[C]
}

trait StringSolutionToolJsonProtocol[C <: ExerciseContent] extends ToolJsonProtocol[String, C] {

  private val stringFormat = Format(Reads.StringReads, Writes.StringWrites)

  override val solutionInputFormat: Format[String] = stringFormat

}

trait FilesSolutionToolJsonProtocol[C <: ExerciseContent] extends ToolJsonProtocol[FilesSolutionInput, C] {

  protected val pathExerciseFileFormat: OFormat[PathExerciseFile] = {

    @unused implicit val fileFormat: Format[File] = Format(
      {
        case JsString(value) => JsSuccess(File(value))
        case _               => JsError()
      },
      file => JsString(file.canonicalPath)
    )

    Json.format
  }

  private val contentExerciseFileFormat: OFormat[ContentExerciseFile] = Json.format

  protected val exerciseFileFormat: OFormat[ExerciseFile] = {
    @unused implicit val peff: OFormat[PathExerciseFile]    = pathExerciseFileFormat
    @unused implicit val ceff: OFormat[ContentExerciseFile] = contentExerciseFileFormat

    Json.format
  }

  protected val filesSolutionFormat: Format[FilesSolution] = {
    @unused implicit val eff: Format[ExerciseFile] = exerciseFileFormat

    Json.format
  }

  override val solutionInputFormat: Format[FilesSolutionInput] = {
    @unused implicit val ceff: OFormat[ContentExerciseFile] = contentExerciseFileFormat

    Json.format
  }

}

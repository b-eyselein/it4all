package model.tools

import better.files.File
import model._
import play.api.libs.json._

trait ToolJsonProtocol[SolutionInputFormat, C <: ExerciseContent, P <: ExPart] {

  protected val keyValueObjectMapFormat: Format[Map[String, String]] = {

    val keyValueObjectFormat: Format[KeyValueObject] = Json.format[KeyValueObject]

    Format(
      Reads.seq(keyValueObjectFormat).map(_.map { case KeyValueObject(key, value) => (key, value) }.toMap),
      Writes.seq(keyValueObjectFormat).contramap(_.toSeq.map { case (key, value) => KeyValueObject(key, value) })
    )
  }

  val partTypeFormat: Format[P]

  val solutionInputFormat: Format[SolutionInputFormat]

  val exerciseContentFormat: OFormat[C]

}

abstract class StringSolutionToolJsonProtocol[C <: ExerciseContent, PartType <: ExPart] extends ToolJsonProtocol[String, C, PartType] {

  private val stringFormat = Format(Reads.StringReads, Writes.StringWrites)

  override val solutionInputFormat: Format[String] = stringFormat

}

abstract class FilesSolutionToolJsonProtocol[C <: ExerciseContent, PartType <: ExPart] extends ToolJsonProtocol[FilesSolutionInput, C, PartType] {

  protected val pathExerciseFileFormat: OFormat[PathExerciseFile] = {

    implicit val fileFormat: Format[File] = Format(
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
    implicit val peff: OFormat[PathExerciseFile]    = pathExerciseFileFormat
    implicit val ceff: OFormat[ContentExerciseFile] = contentExerciseFileFormat

    Json.format
  }

  protected val filesSolutionFormat: Format[FilesSolution] = {
    implicit val eff: Format[ExerciseFile] = exerciseFileFormat

    Json.format
  }

  override val solutionInputFormat: Format[FilesSolutionInput] = {
    implicit val ceff: OFormat[ContentExerciseFile] = contentExerciseFileFormat

    Json.format
  }

}

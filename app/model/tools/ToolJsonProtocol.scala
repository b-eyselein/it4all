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

  lazy val userSolutionFormat: OFormat[UserSolution[SolutionInputFormat, P]] = {
    implicit val ptf: Format[P]                  = partTypeFormat
    implicit val sf: Format[SolutionInputFormat] = solutionInputFormat

    Json.format
  }

  protected val exerciseContentFormat: OFormat[C]

  private val textPartFormat: OFormat[TextPart] = {
    implicit val x0: OFormat[StringTextPart]      = Json.format
    implicit val x1: OFormat[HighlightedTextPart] = Json.format

    Json.format
  }

  private val exerciseTextTextParagraphFormat: OFormat[ExerciseTextTextParagraph] = {
    implicit val x: OFormat[TextPart] = textPartFormat

    Json.format
  }

  private val bulletListPointFormat: OFormat[BulletListPoint] = {
    implicit val x: OFormat[TextPart] = textPartFormat

    Json.format
  }

  private val exerciseTextListParagraphFormat: OFormat[ExerciseTextListParagraph] = {
    implicit val x: OFormat[BulletListPoint] = bulletListPointFormat

    Json.format
  }

  private val exerciseTextParagraphFormat: OFormat[ExerciseTextParagraph] = {
    implicit val x0: OFormat[ExerciseTextTextParagraph] = exerciseTextTextParagraphFormat
    implicit val x1: OFormat[ExerciseTextListParagraph] = exerciseTextListParagraphFormat

    Json.format
  }

  final lazy val exerciseFormat: OFormat[Exercise[C]] = {
    implicit val x0: OFormat[TopicWithLevel]        = JsonProtocols.topicWithLevelFormat
    implicit val x1: OFormat[C]                     = exerciseContentFormat
    implicit val x2: OFormat[ExerciseTextParagraph] = exerciseTextParagraphFormat

    Json.format
  }

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

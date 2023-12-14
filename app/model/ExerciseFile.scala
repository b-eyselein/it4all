package model

import better.files._
import sangria.schema.{ObjectType, fields, Field, StringType, BooleanType, InputObjectType, InputField}
import model.graphql.GraphQLContext

sealed trait ExerciseFile {
  val name: String
  val editable: Boolean
  def content: String

  def writeOrCopyToDirectory(directory: File): File
}

object ExerciseFile {
  val queryType: ObjectType[GraphQLContext, ExerciseFile] = ObjectType(
    "ExerciseFile",
    fields[GraphQLContext, ExerciseFile](
      Field("name", StringType, resolve = _.value.name),
      Field("editable", BooleanType, resolve = _.value.editable),
      Field("content", StringType, resolve = _.value.content)
    )
  )

  val inputType: InputObjectType[ContentExerciseFile] = InputObjectType[ContentExerciseFile](
    "ExerciseFileInput",
    List(
      InputField("name", StringType),
      InputField("editable", BooleanType),
      InputField("content", StringType)
    )
  )
}

final case class ContentExerciseFile(
  name: String,
  content: String,
  editable: Boolean
) extends ExerciseFile {

  def writeOrCopyToDirectory(directory: File): File = {
    val targetPath = directory / name

    targetPath
      .createIfNotExists(createParents = true)
      .write(content)
  }

}

final case class PathExerciseFile(
  name: String,
  directory: File,
  editable: Boolean,
  private val realFilename: Option[String] = None
) extends ExerciseFile {

  def file: File = directory / realFilename.getOrElse(name)

  override def content: String = file.contentAsString

  def writeOrCopyToDirectory(directory: File): File = file.copyTo(directory / name, overwrite = true)

}

package model

import sangria.schema.{ObjectType, fields, ListType, Field}
import model.graphql.GraphQLContext

sealed trait IFilesSolution {
  def files: Seq[ExerciseFile]
}

final case class FilesSolutionInput(
  files: Seq[ContentExerciseFile]
) extends IFilesSolution

final case class FilesSolution(
  files: Seq[ExerciseFile]
) extends IFilesSolution

object FilesSolution {
  val queryType: ObjectType[GraphQLContext, FilesSolution] = ObjectType(
    "FilesSolution",
    fields[GraphQLContext, FilesSolution](
      Field("files", ListType(ExerciseFile.queryType), resolve = _.value.files)
    )
  )
}

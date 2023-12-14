package model

import model.graphql.GraphQLContext
import sangria.schema.{Field, ListType, ObjectType, fields}

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

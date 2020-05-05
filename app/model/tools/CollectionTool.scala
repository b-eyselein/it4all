package model.tools

import better.files.File
import model._
import model.core.result.AbstractCorrectionResult
import model.graphql.ToolGraphQLModelBasics

import scala.concurrent.{ExecutionContext, Future}

abstract class CollectionTool(
  val id: String,
  val name: String,
  val toolState: ToolState = ToolState.LIVE
) {

  // Folders

  def solutionDirForExercise(username: String, collId: Int, exId: Int): File =
    File.currentWorkingDirectory / "data" / id / "solutions" / username / s"$collId" / s"$exId"

  // Abstract types

  type SolType
  type ExContentType <: ExerciseContent[SolType]
  type PartType <: ExPart
  type ResType <: AbstractCorrectionResult

  // Yaml, Html forms, Json, GraphQL

  val jsonFormats: ToolJsonProtocol[SolType, ExContentType, PartType]

  val graphQlModels: ToolGraphQLModelBasics[SolType, ExContentType, PartType, ResType]

  // Other helper methods

  def correctAbstract(
    user: LoggedInUser,
    solution: SolType,
    exercise: Exercise[SolType, ExContentType],
    part: PartType,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[ResType]

  val allTopics: Seq[Topic] = Seq.empty

}

package model.tools

import better.files.File
import initialData.InitialData
import model._
import model.graphql.ToolGraphQLModelBasics

import scala.concurrent.{ExecutionContext, Future}

abstract class Tool(val id: String, val name: String, val isBeta: Boolean = false) {

  // FIXME: tools without parts?
  // abstract class ToolWithParts(id: String, name: String, isBeta: Boolean = false) extends Tool(id, name, isBeta)

  // Abstract types

  type SolutionInputType
  type ExContentType <: ExerciseContent
  type PartType <: ExPart
  type ResType <: AbstractCorrectionResult

  // Json & GraphQL Formats

  val jsonFormats: ToolJsonProtocol[SolutionInputType, ExContentType, PartType]

  val graphQlModels: ToolGraphQLModelBasics[SolutionInputType, ExContentType, PartType, ResType]

  // Other helper methods

  protected def solutionDirForExercise(username: String, collId: Int, exId: Int): File =
    File.currentWorkingDirectory / "data" / id / "solutions" / username / s"$collId" / s"$exId"

  def correctAbstract(
    user: User,
    solution: SolutionInputType,
    exercise: Exercise[ExContentType],
    part: PartType
  )(implicit executionContext: ExecutionContext): Future[ResType]

  val initialData: InitialData[ExContentType]

}

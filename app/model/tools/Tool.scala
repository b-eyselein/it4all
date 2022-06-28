package model.tools

import better.files.File
import initialData.InitialData
import model._
import model.graphql.ToolGraphQLModelBasics
import model.result.AbstractCorrectionResult

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

// Tools

abstract class Tool(val id: String, val name: String, val isBeta: Boolean = false) {

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
  )(implicit executionContext: ExecutionContext): Future[Try[ResType]]

  val allTopics: Seq[Topic] = Seq.empty

  val initialData: InitialData[ExContentType]

}

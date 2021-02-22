package model.tools

import better.files.File
import enumeratum.{EnumEntry, PlayEnum}
import initialData.InitialData
import model._
import model.graphql.ToolGraphQLModelBasics
import model.result.AbstractCorrectionResult

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

// Tool states

sealed trait ToolState extends EnumEntry

object ToolState extends PlayEnum[ToolState] {

  override val values: IndexedSeq[ToolState] = findValues

  case object PRE_ALPHA extends ToolState

  case object ALPHA extends ToolState

  case object BETA extends ToolState

  case object LIVE extends ToolState

}

// Tools

abstract class Tool(val id: String, val name: String, val toolState: ToolState = ToolState.LIVE) {

  // Abstract types

  type SolutionType
  type SolutionInputType
  type ExContentType <: ExerciseContent
  type PartType <: ExPart
  type ResType <: AbstractCorrectionResult

  // Json & GraphQL Formats

  val jsonFormats: ToolJsonProtocol[SolutionType, SolutionInputType, ExContentType, PartType]

  val graphQlModels: ToolGraphQLModelBasics[SolutionInputType, ExContentType, PartType, ResType]

  // Other helper methods

  def solutionDirForExercise(username: String, collId: Int, exId: Int): File =
    File.currentWorkingDirectory / "data" / id / "solutions" / username / s"$collId" / s"$exId"

  def correctAbstract(
    user: LoggedInUser,
    solution: SolutionInputType,
    exercise: Exercise[ExContentType],
    part: PartType
  )(implicit executionContext: ExecutionContext): Future[Try[ResType]]

  val allTopics: Seq[Topic] = Seq.empty

  val initialData: InitialData[ExContentType]

  def possibleParts: Seq[PartType] = Seq.empty

}

package model.tools

import better.files.File
import enumeratum.{EnumEntry, PlayEnum}
import initialData.InitialData
import model.graphql.ToolGraphQLModelBasics
import model.result.AbstractCorrectionResult
import model._

import scala.concurrent.{ExecutionContext, Future}

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

  type SolType
  type ExContentType <: ExerciseContent
  type PartType <: ExPart
  type ResType <: AbstractCorrectionResult

  // Json & GraphQL Formats

  val jsonFormats: ToolJsonProtocol[SolType, ExContentType, PartType]

  val graphQlModels: ToolGraphQLModelBasics[SolType, ExContentType, PartType, ResType]

  // Other helper methods

  def solutionDirForExercise(username: String, collId: Int, exId: Int): File =
    File.currentWorkingDirectory / "data" / id / "solutions" / username / s"$collId" / s"$exId"

  def correctAbstract(
    user: LoggedInUser,
    solution: SolType,
    exercise: Exercise[ExContentType],
    part: PartType
  )(implicit executionContext: ExecutionContext): Future[ResType]

  val allTopics: Seq[Topic] = Seq.empty

  val initialData: InitialData[ExContentType]

  def possibleParts: Seq[PartType] = Seq.empty

}

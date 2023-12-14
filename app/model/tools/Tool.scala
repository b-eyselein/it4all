package model.tools

import better.files.File
import initialData.InitialData
import model._
import model.graphql.{ToolGraphQLModel, ToolWithPartsGraphQLModel}

import scala.concurrent.{ExecutionContext, Future}

sealed trait Tool {

  val id: String
  val name: String
  val isBeta: Boolean

  type SolInputType
  type ExContType <: ExerciseContent
  type ResType <: AbstractCorrectionResult

  val jsonFormats: ToolJsonProtocol[SolInputType, ExContType]

  val graphQlModels: ToolGraphQLModel[SolInputType, ExContType, ResType]

  protected def solutionDirForExercise(username: String, collId: Int, exId: Int): File =
    File.currentWorkingDirectory / "data" / id / "solutions" / username / s"$collId" / s"$exId"

  val initialData: InitialData[ExContType]

}

abstract class ToolWithoutParts(val id: String, val name: String, val isBeta: Boolean = false) extends Tool {

  def correctAbstract(
    user: User,
    solution: SolInputType,
    exercise: Exercise[ExContType]
  )(implicit executionContext: ExecutionContext): Future[ResType]

}

abstract class ToolWithParts(val id: String, val name: String, val isBeta: Boolean = false) extends Tool {

  // Abstract types

  type PartType <: ExPart

  // Json & GraphQL Formats

  override val jsonFormats: ToolJsonProtocol[SolInputType, ExContType]

  override val graphQlModels: ToolWithPartsGraphQLModel[SolInputType, ExContType, ResType, PartType]

  // Other helper methods

  def correctAbstract(
    user: User,
    solution: SolInputType,
    exercise: Exercise[ExContType],
    part: PartType
  )(implicit executionContext: ExecutionContext): Future[ResType]

}

package model.tools

import better.files.File
import initialData.InitialData
import model._
import model.graphql.{ToolGraphQLModelBasics, ToolWithPartsGraphQLModel, ToolWithoutPartsGraphQLModel}

import scala.concurrent.{ExecutionContext, Future}

// FIXME: tools without parts?

sealed trait Tool {

  val id: String
  val name: String
  val isBeta: Boolean

  type SolInputType
  type ExContType <: ExerciseContent
  type ResType <: AbstractCorrectionResult

  val jsonFormats: ToolJsonProtocol[SolInputType, ExContType]

  val graphQlModels: ToolGraphQLModelBasics[SolInputType, ExContType, ResType]

  protected def solutionDirForExercise(username: String, collId: Int, exId: Int): File =
    File.currentWorkingDirectory / "data" / id / "solutions" / username / s"$collId" / s"$exId"

  val initialData: InitialData[ExContType]

}

abstract class ToolWithoutParts(val id: String, val name: String, val isBeta: Boolean = false) extends Tool {

  // Json & GraphQL Formats

  val jsonFormats: ToolWithoutPartsJsonProtocol[SolInputType, ExContType]

  val graphQlModels: ToolWithoutPartsGraphQLModel[SolInputType, ExContType, ResType]

  // Other helper methods

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

  val jsonFormats: ToolWithPartsJsonProtocol[SolInputType, ExContType, PartType]

  val graphQlModels: ToolWithPartsGraphQLModel[SolInputType, ExContType, ResType, PartType]

  // Other helper methods

  def correctAbstract(
    user: User,
    solution: SolInputType,
    exercise: Exercise[ExContType],
    part: PartType
  )(implicit executionContext: ExecutionContext): Future[ResType]

}
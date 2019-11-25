package model.tools.collectionTools

import model._
import model.core.result.{CompleteResult, EvaluationResult}
import model.core.{LongText, LongTextJsonProtocol}
import model.points.Points
import play.api.libs.json.{Format, Json, Writes}

object ToolJsonProtocol {

  val semanticVersionFormat: Format[SemanticVersion] = Json.format[SemanticVersion]

  val collectionFormat: Format[ExerciseCollection] = Json.format[ExerciseCollection]

  val exerciseBasicsFormat: Format[ApiExerciseBasics] = {
    implicit val svf: Format[SemanticVersion] = semanticVersionFormat

    Json.format[ApiExerciseBasics]
  }

  val exerciseFormat: Format[Exercise] = {
    implicit val scf: Format[SemanticVersion] = semanticVersionFormat
    implicit val ltf: Format[LongText]        = LongTextJsonProtocol.format

    Json.format[Exercise]
  }

  val pointsFormat: Format[Points] = Json.format[Points]

}


trait ToolJsonProtocol[
  PartType <: ExPart, EC <: ExerciseContent,
  ST, UST <: UserSolution[PartType, ST],
  CR <: CompleteResult[_ <: EvaluationResult]
] {

  val exerciseContentFormat: Format[EC]

  val userSolutionFormat: Format[UST]

  val completeResultWrites: Writes[CR]

}

abstract class StringSampleSolutionToolJsonProtocol[
  PartType <: ExPart, E <: StringExerciseContent, CR <: CompleteResult[_ <: EvaluationResult]
](partTypeFormat: Format[PartType])
  extends ToolJsonProtocol[PartType, E, String, StringUserSolution[PartType], CR] {

  protected val sampleSolutionFormat: Format[StringSampleSolution] = Json.format[StringSampleSolution]

  override val userSolutionFormat: Format[StringUserSolution[PartType]] = {
    implicit val ptf: Format[PartType] = partTypeFormat
    implicit val pf : Format[Points]   = ToolJsonProtocol.pointsFormat

    Json.format[StringUserSolution[PartType]]
  }

}


abstract class FilesSampleSolutionToolJsonProtocol[
  PartType <: ExPart, E <: FileExerciseContent, CR <: CompleteResult[_ <: EvaluationResult]
](partTypeFormat: Format[PartType])
  extends ToolJsonProtocol[PartType, E, Seq[ExerciseFile], FilesUserSolution[PartType], CR] {

  protected val sampleSolutionFormat: Format[FilesSampleSolution] = {
    implicit val eff: Format[ExerciseFile] = ExerciseFileJsonProtocol.exerciseFileFormat

    Json.format[FilesSampleSolution]
  }

  override val userSolutionFormat: Format[FilesUserSolution[PartType]] = {
    implicit val ptf: Format[PartType]     = partTypeFormat
    implicit val pf : Format[Points]       = ToolJsonProtocol.pointsFormat
    implicit val eff: Format[ExerciseFile] = ExerciseFileJsonProtocol.exerciseFileFormat

    Json.format[FilesUserSolution[PartType]]
  }

}

package model.tools.collectionTools

import model.core.result.{CompleteResult, EvaluationResult}
import model.points.Points
import play.api.libs.json._

object ToolJsonProtocol {

  val semanticVersionFormat: Format[SemanticVersion] = Json.format

  val collectionFormat: Format[ExerciseCollection] = Json.format

  val exTagFormat: Format[ExTag] = Json.format

  val exerciseMetaDataFormat: Format[ExerciseMetaData] = {
    implicit val scf: Format[SemanticVersion] = semanticVersionFormat
    implicit val etf: Format[ExTag]           = exTagFormat

    Json.format
  }

  val exerciseFormat: Format[Exercise] = {
    implicit val scf: Format[SemanticVersion] = semanticVersionFormat
    implicit val etf: Format[ExTag]           = exTagFormat

    Json.format
  }

  val pointsFormat: Format[Points] = Format(
    (jsValue: JsValue) => Reads.DoubleReads.reads(jsValue).map(p => Points((p * 4).floor.toInt)),
    (points: Points) => Writes.DoubleWrites.writes(points.asDouble)
  )

  val exerciseFileFormat: Format[ExerciseFile] = Json.format

}


trait ToolJsonProtocol[
  EC <: ExerciseContent, ST, CR <: CompleteResult[_ <: EvaluationResult]
] {

  val exerciseContentFormat: Format[EC]

  val solutionFormat: Format[ST]

  val completeResultWrites: Writes[CR]

}

abstract class StringSampleSolutionToolJsonProtocol[
  E <: StringExerciseContent, CR <: CompleteResult[_ <: EvaluationResult]
] extends ToolJsonProtocol[E, String, CR] {

  override val solutionFormat: Format[String] = Format(Reads.StringReads, Writes.StringWrites)

  protected val sampleSolutionFormat: Format[SampleSolution[String]] = Json.format[SampleSolution[String]]

}


abstract class FilesSampleSolutionToolJsonProtocol[
  E <: FileExerciseContent, CR <: CompleteResult[_ <: EvaluationResult]
] extends ToolJsonProtocol[E, Seq[ExerciseFile], CR] {

  override val solutionFormat: Format[Seq[ExerciseFile]] = Format(
    Reads.seq(ToolJsonProtocol.exerciseFileFormat),
    Writes.seq(ToolJsonProtocol.exerciseFileFormat)
  )

  protected val sampleSolutionFormat: Format[SampleSolution[Seq[ExerciseFile]]] = {
    implicit val eff: Format[Seq[ExerciseFile]] = solutionFormat

    Json.format[SampleSolution[Seq[ExerciseFile]]]
  }

}

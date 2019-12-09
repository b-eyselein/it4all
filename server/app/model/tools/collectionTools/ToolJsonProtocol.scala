package model.tools.collectionTools

import model.core.result.{CompleteResult, EvaluationResult, SuccessType}
import model.points.Points
import nl.codestar.scalatsi.TypescriptType._
import nl.codestar.scalatsi.{DefaultTSTypes, TSIType, TSType}
import play.api.libs.json._

object ToolJsonProtocol {

  val semanticVersionFormat: Format[SemanticVersion] = Json.format[SemanticVersion]

  val collectionFormat: Format[ExerciseCollection] = Json.format[ExerciseCollection]

  val exTagFormat: Format[ExTag] = Json.format[ExTag]

  val exerciseFormat: Format[Exercise] = {
    implicit val scf: Format[SemanticVersion] = semanticVersionFormat
    implicit val etf: Format[ExTag]           = exTagFormat

    Json.format[Exercise]
  }

  val pointsFormat: Format[Points] = Format(
    (jsValue: JsValue) => Reads.DoubleReads.reads(jsValue).map(p => Points((p * 4).floor.toInt)),
    (points: Points) => Writes.DoubleWrites.writes(points.asDouble)
  )

  val exerciseFileFormat: Format[ExerciseFile] = Json.format[ExerciseFile]

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

trait ToolTSInterfaceTypes extends DefaultTSTypes {

  import nl.codestar.scalatsi.dsl._

  def enumTsType[E <: enumeratum.EnumEntry, P <: enumeratum.Enum[E]](companion: P): TSType[E] =
    TSType.alias(companion.getClass.getSimpleName.replace("$", ""), TSUnion(companion.values.map(_.entryName)))

  val jsValueTsType: TSType[JsValue] = TSType.sameAs[JsValue, Any]

  val exerciseFileTSI: TSIType[ExerciseFile] = TSType.fromCaseClass[ExerciseFile] + ("active?" -> TSBoolean.get)

  val successTypeTS: TSType[SuccessType] = enumTsType(SuccessType)

  def sampleSolutionTSI[SolType](solTypeTSI: TSType[SolType])(implicit x: Manifest[SampleSolution[SolType]]): TSIType[SampleSolution[SolType]] = {
    //    implicit val eft: TSIType[ExerciseFile] = exerciseFileTSI
    //    implicit val stt: TSType[SolType]       = solTypeTSI

    TSType.interface[SampleSolution[SolType]](
      "id" -> TSNumber,
      "sample" -> TSObject // solTypeTSI.get
    )
    //    TSType.fromCaseClass[SampleSolution[SolType]]
  }

  // Collections, Exercises and ExerciseContents

  implicit val exerciseTSI: TSIType[Exercise] = {
    implicit val svt : TSIType[SemanticVersion] = TSType.fromCaseClass[SemanticVersion]
    implicit val jvtt: TSType[JsValue]          = jsValueTsType
    implicit val ett : TSIType[ExTag]           = TSType.fromCaseClass[ExTag]

    TSType.fromCaseClass[Exercise]
  }

  implicit val exerciseCollectionTSI: TSIType[ExerciseCollection] =
    TSType.fromCaseClass[ExerciseCollection] + ("exercises" -> TSArray(exerciseTSI.get))


}

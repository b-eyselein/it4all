package model.tools.collectionTools

import model.core.result.SuccessType
import nl.codestar.scalatsi.TypescriptType._
import nl.codestar.scalatsi.{DefaultTSTypes, TSIType, TSType}
import play.api.libs.json.JsValue


trait ToolTSInterfaceTypes extends DefaultTSTypes {

  import nl.codestar.scalatsi.dsl._

  def enumTsType[E <: enumeratum.EnumEntry, P <: enumeratum.Enum[E]](companion: P): TSType[E] =
    TSType.alias(companion.getClass.getSimpleName.replace("$", ""), TSUnion(companion.values.map(_.entryName)))

  val jsValueTsType: TSType[JsValue] = TSType.sameAs[JsValue, Any](anyTSType)

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

  val exerciseMetaDataTSI: TSIType[ExerciseMetaData] = {
    implicit val svt: TSIType[SemanticVersion] = TSType.fromCaseClass[SemanticVersion]
    implicit val ett: TSIType[ExTag]           = TSType.fromCaseClass[ExTag]

    TSType.fromCaseClass
  }

  val exerciseTSI: TSIType[Exercise] = {
    implicit val svt : TSIType[SemanticVersion] = TSType.fromCaseClass[SemanticVersion]
    implicit val ett : TSIType[ExTag]           = TSType.fromCaseClass[ExTag]
    implicit val jvtt: TSType[JsValue]          = jsValueTsType

    TSType.fromCaseClass[Exercise]
  }

  val exerciseCollectionTSI: TSIType[ExerciseCollection] = TSType.fromCaseClass[ExerciseCollection]


}

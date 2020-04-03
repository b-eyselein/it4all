package model.tools.collectionTools

import nl.codestar.scalatsi.TypescriptType.TypescriptNamedType
import nl.codestar.scalatsi.{TSIType, TSType}
import play.api.libs.json.JsValue

object BasicTSTypes extends ToolTSInterfaceTypes {

  private val semanticVersionTSI: TSIType[SemanticVersion] = TSType.fromCaseClass
  private val exTagTSI: TSIType[ExTag]                     = TSType.fromCaseClass

  private val exerciseMetaDataTSI: TSIType[ExerciseMetaData] = {
    implicit val svt: TSIType[SemanticVersion] = semanticVersionTSI
    implicit val ett: TSIType[ExTag]           = exTagTSI

    TSType.fromCaseClass
  }

  private val exerciseTSI: TSIType[Exercise] = {
    implicit val svt: TSIType[SemanticVersion] = semanticVersionTSI
    implicit val ett: TSIType[ExTag]           = exTagTSI
    implicit val jvtt: TSType[JsValue]         = jsValueTsType

    TSIType(TSType.fromCaseClass[Exercise].get.copy(extending = Some(exerciseMetaDataTSI.get)))
  }

  private val exerciseCollectionTSI: TSIType[ExerciseCollection] = TSType.fromCaseClass[ExerciseCollection]

  val exported: Seq[TypescriptNamedType] = Seq(
    exerciseTSI.get,
    exerciseMetaDataTSI.get,
    exerciseCollectionTSI.get
  )

}

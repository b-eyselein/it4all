package model.tools.collectionTools

import nl.codestar.scalatsi.TypescriptType.TypescriptNamedType
import nl.codestar.scalatsi.{DefaultTSTypes, TSIType, TSType}
import play.api.libs.json.JsValue

object BasicTSTypes extends DefaultTSTypes {

  private val exerciseTSI: TSIType[Exercise] = {
    implicit val svt: TSIType[SemanticVersion] = TSType.fromCaseClass
    implicit val ett: TSIType[ExTag]           = TSType.fromCaseClass
    implicit val jvtt: TSType[JsValue]         = TSType.sameAs[JsValue, Any](anyTSType)

    TSType.fromCaseClass
  }

  private val exerciseCollectionTSI: TSIType[ExerciseCollection] = TSType.fromCaseClass[ExerciseCollection]

  val exported: Seq[TypescriptNamedType] = Seq(
    exerciseTSI.get,
    exerciseCollectionTSI.get
  )

}

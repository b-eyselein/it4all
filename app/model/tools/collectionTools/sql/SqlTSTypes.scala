package model.tools.collectionTools.sql

import nl.codestar.scalatsi.TypescriptType.TypescriptNamedType
import nl.codestar.scalatsi.{DefaultTSTypes, TSIType, TSType}

object SqlTSTypes extends DefaultTSTypes {

  private val sqlQueryResultTSI: TSIType[SqlQueryResult] = {
    implicit val sct: TSType[SqlCell] = TSType.fromCaseClass
    implicit val srt: TSType[SqlRow]  = TSType.fromCaseClass

    TSType.fromCaseClass
  }

  val exported: Seq[TypescriptNamedType] = Seq(
    sqlQueryResultTSI.get
  )

}

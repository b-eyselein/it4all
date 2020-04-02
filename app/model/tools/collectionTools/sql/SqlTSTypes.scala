package model.tools.collectionTools.sql

import model.core.matching.MatchType
import model.tools.collectionTools.ToolTSInterfaceTypes
import nl.codestar.scalatsi.TypescriptType.TypescriptNamedType
import nl.codestar.scalatsi.{TSIType, TSType}

object SqlTSTypes extends ToolTSInterfaceTypes {

  private implicit val matchTypeTsType: TSType[MatchType] = matchTypeTS

  private val sqlQueryResultTSI: TSIType[SqlQueryResult] = {
    implicit val sct: TSType[SqlCell] = TSType.fromCaseClass
    implicit val srt: TSType[SqlRow]  = TSType.fromCaseClass

    TSType.fromCaseClass
  }

  val exported: Seq[TypescriptNamedType] = Seq(
    sqlQueryResultTSI.get
  )

}

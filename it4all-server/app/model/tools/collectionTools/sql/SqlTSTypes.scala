package model.tools.collectionTools.sql

import model.tools.collectionTools.{SampleSolution, ToolTSInterfaceTypes}
import nl.codestar.scalatsi.TypescriptType.TSString
import nl.codestar.scalatsi.{TSIType, TSType}

trait SqlTSTypes extends ToolTSInterfaceTypes {

  import nl.codestar.scalatsi.TypescriptType.TSInterface

  val sqlExerciseContentTSI: TSIType[SqlExerciseContent] = {
    implicit val seTypeT: TSType[SqlExerciseType]         = enumTsType(SqlExerciseType)
    implicit val seTagT : TSType[SqlExerciseTag]          = enumTsType(SqlExerciseTag)
    implicit val ssst   : TSIType[SampleSolution[String]] = sampleSolutionTSI[String](TSType(TSString))

    TSType.fromCaseClass
  }

  val sqlQueryResultTSI: TSIType[SqlQueryResult] = {
    implicit val sct: TSType[SqlCell] = TSType.fromCaseClass
    implicit val srt: TSType[SqlRow]  = TSType.fromCaseClass

    TSType.fromCaseClass
  }

}

package model.tools.collectionTools.sql

import model.tools.collectionTools.{SampleSolution, ToolTSInterfaceTypes}
import nl.codestar.scalatsi.TypescriptType.{TSInterface, TSString}
import nl.codestar.scalatsi.{TSIType, TSType}

trait SqlTSTypes extends ToolTSInterfaceTypes {

  val sqlExerciseContentTSI: TSIType[SqlExerciseContent] = {
    implicit val seTypeT: TSType[SqlExerciseType]         = enumTsType(SqlExerciseType)
    implicit val seTagT : TSType[SqlExerciseTag]          = enumTsType(SqlExerciseTag)
    implicit val ssst   : TSIType[SampleSolution[String]] = sampleSolutionTSI[String](TSType(TSString))

    TSType.fromCaseClass[SqlExerciseContent]
  }

}

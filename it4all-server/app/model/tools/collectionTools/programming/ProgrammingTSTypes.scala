package model.tools.collectionTools.programming

import model.tools.collectionTools.{ExerciseFile, ToolTSInterfaceTypes}
import nl.codestar.scalatsi.{TSIType, TSType}
import play.api.libs.json.JsValue

trait ProgrammingTSTypes extends ToolTSInterfaceTypes {

  import nl.codestar.scalatsi.TypescriptType.TSInterface

  //  implicit val progExerciseContentTSI: TSIType[ProgExerciseContent] = {
  //
  //   TSType.fromCaseClass[ProgExerciseContent]
  //}

  implicit val progSolutionTSI: TSIType[ProgSolution] = {
    implicit val eft : TSIType[ExerciseFile] = exerciseFileTSI
    implicit val ptdt: TSIType[ProgTestData] = {
      implicit val jvt: TSType[JsValue] = jsValueTsType
      TSType.fromCaseClass[ProgTestData]
    }

    TSType.fromCaseClass[ProgSolution]
  }

}

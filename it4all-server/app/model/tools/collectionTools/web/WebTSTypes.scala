package model.tools.collectionTools.web

import de.uniwue.webtester.sitespec.{HtmlTask, JsActionType, JsHtmlElementSpec, JsTask}
import model.core.result.SuccessType
import model.tools.collectionTools.{ExerciseFile, SampleSolution, ToolTSInterfaceTypes}
import nl.codestar.scalatsi.TypescriptType.TSArray
import nl.codestar.scalatsi.{TSIType, TSNamedType, TSType}

trait WebTSTypes extends ToolTSInterfaceTypes {

  // TODO: do not remove this import: import nl.codestar.scalatsi.TypescriptType.TSInterface

  import nl.codestar.scalatsi.TypescriptType.TSInterface

  private val jsActionTypeTSType: TSType[JsActionType] = enumTsType(JsActionType)

  private val jsTaskTSType: TSIType[JsTask] = {
    implicit val jatt : TSType[JsActionType]       = jsActionTypeTSType
    implicit val jhest: TSIType[JsHtmlElementSpec] = TSType.fromCaseClass[JsHtmlElementSpec]
    TSType.fromCaseClass[JsTask]
  }

  val webExerciseContentTSI: TSIType[WebExerciseContent] = {
    implicit val eft : TSIType[ExerciseFile]                      = exerciseFileTSI
    implicit val htt : TSIType[HtmlTask]                          = TSType.fromCaseClass[HtmlTask]
    implicit val jtt : TSIType[JsTask]                            = jsTaskTSType
    implicit val fsst: TSIType[SampleSolution[Seq[ExerciseFile]]] = sampleSolutionTSI(TSType(TSArray(exerciseFileTSI.get)))


    TSType.fromCaseClass[WebExerciseContent]
  }

  private val gradedTextResultTSType = TSType.fromCaseClass[GradedTextResult]

  private val gradedHtmlTaskResult = {
    implicit val stt : TSType[SuccessType]           = successTypeTS
    implicit val grst: TSNamedType[GradedTextResult] = gradedTextResultTSType

    TSType.fromCaseClass[GradedHtmlTaskResult]
  }

  private val gradedJsHtmlElementSpecResultTSI: TSIType[GradedJsHtmlElementSpecResult] = {
    implicit val stt : TSType[SuccessType]           = successTypeTS
    implicit val grst: TSNamedType[GradedTextResult] = gradedTextResultTSType

    TSType.fromCaseClass[GradedJsHtmlElementSpecResult]
  }

  private val gradedJsTaskResultTSI: TSIType[GradedJsTaskResult] = {
    implicit val stt : TSType[SuccessType]                    = successTypeTS
    implicit val jatt: TSType[JsActionType]                   = jsActionTypeTSType
    implicit val gest: TSIType[GradedJsHtmlElementSpecResult] = gradedJsHtmlElementSpecResultTSI

    TSType.fromCaseClass[GradedJsTaskResult]
  }

  val webCompleteResultTSI: TSIType[WebCompleteResult] = {
    implicit val ghtt: TSIType[GradedHtmlTaskResult] = gradedHtmlTaskResult
    implicit val gjtt: TSIType[GradedJsTaskResult]   = gradedJsTaskResultTSI

    TSType.fromCaseClass[WebCompleteResult]
  }

}

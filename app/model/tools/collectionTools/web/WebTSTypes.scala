package model.tools.collectionTools.web

import de.uniwue.webtester.sitespec.JsActionType
import model.core.result.SuccessType
import model.tools.collectionTools.ToolTSInterfaceTypes
import nl.codestar.scalatsi.TypescriptType.TypescriptNamedType
import nl.codestar.scalatsi.{TSIType, TSNamedType, TSType}

object WebTSTypes extends ToolTSInterfaceTypes {

  // TODO: do not remove this import: import nl.codestar.scalatsi.TypescriptType.TSInterface

  private val jsActionTypeTSType: TSType[JsActionType] = enumTsType(JsActionType)

  private val gradedTextResultTSType = TSType.fromCaseClass[GradedTextResult]

  private val gradedHtmlTaskResult = {
    implicit val stt: TSType[SuccessType]            = successTypeTS
    implicit val grst: TSNamedType[GradedTextResult] = gradedTextResultTSType

    TSType.fromCaseClass[GradedHtmlTaskResult]
  }

  private val gradedJsHtmlElementSpecResultTSI: TSIType[GradedJsHtmlElementSpecResult] = {
    implicit val stt: TSType[SuccessType]            = successTypeTS
    implicit val grst: TSNamedType[GradedTextResult] = gradedTextResultTSType

    TSType.fromCaseClass[GradedJsHtmlElementSpecResult]
  }

  private val gradedJsTaskResultTSI: TSIType[GradedJsTaskResult] = {
    implicit val stt: TSType[SuccessType]                     = successTypeTS
    implicit val jatt: TSType[JsActionType]                   = jsActionTypeTSType
    implicit val gest: TSIType[GradedJsHtmlElementSpecResult] = gradedJsHtmlElementSpecResultTSI

    TSType.fromCaseClass[GradedJsTaskResult]
  }

  private val webCompleteResultTSI: TSIType[WebCompleteResult] = {
    implicit val ghtt: TSIType[GradedHtmlTaskResult] = gradedHtmlTaskResult
    implicit val gjtt: TSIType[GradedJsTaskResult]   = gradedJsTaskResultTSI

    TSType.fromCaseClass[WebCompleteResult]
  }

  val exported: Seq[TypescriptNamedType] = Seq(
    webCompleteResultTSI.get
  )

}

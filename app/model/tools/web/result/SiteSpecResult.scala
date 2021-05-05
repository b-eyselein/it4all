package model.tools.web.result

import model.tools.web.sitespec.{HtmlTask, JsHtmlElementSpec, JsTask}

sealed trait WebTaskResult

// Html Results

final case class HtmlTaskResult(
  htmlTask: HtmlTask,
  elementSpecResult: ElementSpecResult[HtmlTask]
) extends WebTaskResult

// Javascript Results

final case class JsTaskResult(
  jsTask: JsTask,
  preResults: Seq[ElementSpecResult[JsHtmlElementSpec]],
  actionResult: JsActionResult,
  postResults: Seq[ElementSpecResult[JsHtmlElementSpec]]
) extends WebTaskResult

// Complete Site Spec

final case class SiteSpecResult(
  htmlResults: Seq[HtmlTaskResult],
  jsResults: Seq[JsTaskResult]
)

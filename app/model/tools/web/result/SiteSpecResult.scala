package model.tools.web.result

import model.tools.web.sitespec.{HtmlTask, JsTask}

sealed trait WebTaskResult

// Html Results

final case class HtmlTaskResult(
  htmlTask: HtmlTask,
  elementSpecResult: ElementSpecResult
) extends WebTaskResult

// Javascript Results

final case class JsTaskResult(
  jsTask: JsTask,
  preResults: Seq[ElementSpecResult],
  actionResult: JsActionResult,
  postResults: Seq[ElementSpecResult]
) extends WebTaskResult

// Complete Site Spec

final case class SiteSpecResult(
  htmlResults: Seq[HtmlTaskResult],
  jsResults: Seq[JsTaskResult]
)

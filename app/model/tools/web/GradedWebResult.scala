package model.tools.web

import model.points._
import model.result.{AbstractCorrectionResult, SuccessType}
import model.tools.web.sitespec.JsAction

final case class WebResult(
  gradedHtmlTaskResults: Seq[GradedHtmlTaskResult],
  gradedJsTaskResults: Seq[GradedJsTaskResult],
  points: Points,
  maxPoints: Points
) extends AbstractCorrectionResult {

  override def isCompletelyCorrect: Boolean = {

    val htmlTasksOk = gradedHtmlTaskResults.forall(_.elementSpecResult.success == SuccessType.COMPLETE)
    val jsTasksOk   = gradedJsTaskResults.forall(_.success == SuccessType.COMPLETE)

    htmlTasksOk && jsTasksOk
  }

}

final case class GradedTextResult(
  keyName: String,
  awaitedContent: String,
  maybeFoundContent: Option[String],
  isSuccessful: Boolean,
  points: Points,
  maxPoints: Points
)

final case class GradedElementSpecResult(
  success: SuccessType,
  elementFound: Boolean,
  textContentResult: Option[GradedTextResult],
  attributeResults: Seq[GradedTextResult],
  points: Points,
  maxPoints: Points
)

// Html & CSS Results

final case class GradedHtmlTaskResult(
  id: Int,
  elementSpecResult: GradedElementSpecResult
)

// Javascript Results

final case class GradedJsActionResult(actionPerformed: Boolean, jsAction: JsAction, points: Points, maxPoints: Points)

final case class GradedJsTaskResult(
  id: Int,
  gradedPreResults: Seq[GradedElementSpecResult],
  gradedJsActionResult: GradedJsActionResult,
  gradedPostResults: Seq[GradedElementSpecResult],
  success: SuccessType,
  points: Points,
  maxPoints: Points
)

package model.tools.web

import de.uniwue.webtester.sitespec.{JsAction, JsActionType}
import model.core.result.{AbstractCorrectionResult, SuccessType}
import model.points._

sealed trait WebAbstractResult extends AbstractCorrectionResult

final case class WebInternalErrorResult(
  solutionSaved: Boolean,
  maxPoints: Points
) extends WebAbstractResult {

  override def points: Points = zeroPoints

}

final case class WebCompleteResult(
  gradedHtmlTaskResults: Seq[GradedHtmlTaskResult],
  gradedJsTaskResults: Seq[GradedJsTaskResult],
  points: Points,
  maxPoints: Points,
  solutionSaved: Boolean
) extends WebAbstractResult

sealed trait GradedWebTaskResult

final case class GradedTextResult(
  keyName: String,
  awaitedContent: String,
  maybeFoundContent: Option[String],
  isSuccessful: Boolean,
  points: Points,
  maxPoints: Points
)

sealed trait GradedElementSpecResult {
  val id: Int
  val success: SuccessType
  val elementFound: Boolean
  val textContentResult: Option[GradedTextResult]
  val attributeResults: Seq[GradedTextResult]
  val isSuccessful: Boolean
  val points: Points
  val maxPoints: Points
}

// Html & CSS Results

final case class GradedHtmlTaskResult(
  id: Int,
  success: SuccessType,
  elementFound: Boolean,
  textContentResult: Option[GradedTextResult],
  attributeResults: Seq[GradedTextResult],
  isSuccessful: Boolean,
  points: Points,
  maxPoints: Points
) extends GradedWebTaskResult
    with GradedElementSpecResult

// Javascript Results

final case class GradedJsActionResult(actionPerformed: Boolean, jsAction: JsAction, points: Points, maxPoints: Points) {

  def actionDescription: String = jsAction.actionType match {
    case JsActionType.Click => s"Klicke auf Element mit XPath Query <code>${jsAction.xpathQuery}</code>"
    case JsActionType.FillOut =>
      s"Sende Keys '${jsAction.keysToSend.getOrElse("")}' an Element mit XPath Query ${jsAction.xpathQuery}"
  }

}

final case class GradedJsHtmlElementSpecResult(
  id: Int,
  success: SuccessType,
  elementFound: Boolean,
  textContentResult: Option[GradedTextResult],
  attributeResults: Seq[GradedTextResult],
  isSuccessful: Boolean,
  points: Points,
  maxPoints: Points
) extends GradedElementSpecResult

final case class GradedJsTaskResult(
  id: Int,
  gradedPreResults: Seq[GradedJsHtmlElementSpecResult],
  gradedJsActionResult: GradedJsActionResult,
  gradedPostResults: Seq[GradedJsHtmlElementSpecResult],
  success: SuccessType,
  points: Points,
  maxPoints: Points
) extends GradedWebTaskResult

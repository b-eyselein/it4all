package model.tools.web

import de.uniwue.webtester.{JsAction, JsActionType}
import model.core.result.{CompleteResult, EvaluationResult, SuccessType}
import model.points._
import org.openqa.selenium.WebElement

final case class WebCompleteResult(learnerSolution: String, exercise: WebExercise, part: WebExPart,
                                   gradedHtmlTaskResults: Seq[GradedHtmlTaskResult], gradedJsTaskResults: Seq[GradedJsTaskResult])
  extends CompleteResult[GradedWebTaskResult] {

  override type SolType = String

  override def results: Seq[GradedWebTaskResult] = gradedHtmlTaskResults ++ gradedJsTaskResults

  override def points: Points = part match {
    case WebExParts.HtmlPart => addUp(gradedHtmlTaskResults.map(_.points))
    case WebExParts.JsPart   => addUp(gradedJsTaskResults.map(_.points))
  }

  override def maxPoints: Points = exercise.maxPoints(part)

}

sealed trait GradedWebTaskResult extends EvaluationResult


// Html & CSS Results

final case class GradedHtmlTaskResult(gradedElementSpecResult: GradedElementSpecResult, success: SuccessType) extends GradedWebTaskResult {

  def points = gradedElementSpecResult.points

  def maxPoints = gradedElementSpecResult.maxPoints

}

final case class GradedElementSpecResult(id: Int, foundElement: Option[WebElement],
                                         textContentResult: Option[GradedTextResult],
                                         attributeResults: Seq[GradedTextResult],
                                         isSuccessful: Boolean, points: Points, maxPoints: Points)

// Text Results: TextContent, AttributeResult

final case class GradedTextResult(keyName: String, awaitedContent: String, maybeFoundContent: Option[String],
                                  isSuccessful: Boolean, points: Points, maxPoints: Points)

// Javascript Results

final case class GradedJsTaskResult(id: Int, gradedPreResults: Seq[GradedElementSpecResult],
                                    gradedJsActionResult: GradedJsActionResult, gradedPostResults: Seq[GradedElementSpecResult],
                                    success: SuccessType, points: Points, maxPoints: Points) extends GradedWebTaskResult

final case class GradedJsActionResult(actionPerformed: Boolean, jsAction: JsAction) {

  def actionDescription: String = jsAction.actionType match {
    case JsActionType.Click   => s"Klicke auf Element mit XPath Query <code>${jsAction.xpathQuery}</code>"
    case JsActionType.FillOut => s"Sende Keys '${jsAction.keysToSend.getOrElse("")}' an Element mit XPath Query ${jsAction.xpathQuery}"
  }

}

package model.tools.collectionTools.web

import de.uniwue.webtester._
import model.core.result.SuccessType
import model.points._

object WebGrader {

  private val pointForCorrectTextResult: Points = singlePoint
  private val maxPointsForAction       : Points = singlePoint

  private def gradeTextResult(tcr: TextResult): GradedTextResult = {

    val keyName = tcr match {
      case AttributeResult(key, _, _) => key
      case TextContentResult(_, _)    => "TextContent"
    }

    val points = if (tcr.success) pointForCorrectTextResult else zeroPoints

    GradedTextResult(keyName, tcr.awaitedContent, tcr.maybeFoundContent, tcr.success, points, maxPoints = singlePoint)
  }

  private def calculateMaxPointsForElementSpec(element: HtmlElementSpec): Points = {
    val pointsForElement = singlePoint

    val pointsForTextContentResult = element.awaitedTextContent match {
      case None    => zeroPoints
      case Some(_) => pointForCorrectTextResult
    }

    val pointsForAttributes: Seq[Points] = element.attributes.map(_ => pointForCorrectTextResult).toSeq

    pointsForElement + pointsForTextContentResult + addUp(pointsForAttributes)
  }

  private def gradeElementSpecResult(elementSpecResult: ElementSpecResult): GradedElementSpecResult = {
    val maxPoints = calculateMaxPointsForElementSpec(elementSpecResult.elementSpec)

    elementSpecResult match {
      case ElementFoundElementSpecResult(_, foundElement, textContentResult, attributeResults) =>

        val maybeGradedTextContentResult: Option[GradedTextResult] = textContentResult.map(gradeTextResult)

        val gradedAttributeResults: Seq[GradedTextResult] = attributeResults.map(gradeTextResult)

        val pointsForElement           = singlePoint
        val pointsForTextContentResult = maybeGradedTextContentResult.map(_.points).getOrElse(zeroPoints)
        val pointsForAttributeResults  = addUp(gradedAttributeResults.map(_.points))

        val points = pointsForElement + pointsForTextContentResult + pointsForAttributeResults

        val allAttributeResultsSuccessful = attributeResults.forall(_.success)

        val successType: SuccessType = if (allAttributeResultsSuccessful) {
          textContentResult match {
            case None             => SuccessType.COMPLETE
            case Some(textResult) => if (textResult.success) SuccessType.COMPLETE else SuccessType.PARTIALLY
          }
        } else SuccessType.PARTIALLY

        val isSuccessful = successType == SuccessType.COMPLETE

        GradedElementSpecResult(elementSpecResult.elementSpec.id, successType, Some(foundElement), maybeGradedTextContentResult, gradedAttributeResults, isSuccessful, points, maxPoints)

      case _ => GradedElementSpecResult(elementSpecResult.elementSpec.id, SuccessType.NONE, None, None, Seq[GradedTextResult](), isSuccessful = false, zeroPoints, maxPoints)
    }
  }

  private def gradeActionResult(result: JsActionResult): GradedJsActionResult = {
    val points = if (result.success) maxPointsForAction else zeroPoints
    GradedJsActionResult(result.success, result.jsAction, points, maxPointsForAction)
  }

  def gradeHtmlTaskResult(htr: HtmlTaskResult): GradedHtmlTaskResult = {

    val gradedElementSpecResult = gradeElementSpecResult(htr.elementSpecResult)

    GradedHtmlTaskResult(gradedElementSpecResult, gradedElementSpecResult.successType)
  }

  def gradeJsTaskResult(jtr: JsTaskResult): GradedJsTaskResult = {

    val gradedPreResults   = jtr.preResults.map(gradeElementSpecResult)
    val gradedActionResult = gradeActionResult(jtr.actionResult)
    val gradedPostResults  = jtr.postResults.map(gradeElementSpecResult)

    val points: Points = addUp(gradedPreResults.map(_.points)) + gradedActionResult.points + addUp(gradedPostResults.map(_.points))

    val maxPoints: Points = addUp(gradedPreResults.map(_.maxPoints)) + gradedActionResult.maxPoints + addUp(gradedPostResults.map(_.maxPoints))

    val success: SuccessType = SuccessType.ofBool(
      jtr.preResults.forall(_.success) && jtr.actionResult.success && jtr.postResults.forall(_.success)
    )

    GradedJsTaskResult(jtr.jsTask.id, gradedPreResults, gradedActionResult, gradedPostResults, success, points, maxPoints)
  }

}

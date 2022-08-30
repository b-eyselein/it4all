package model.tools.web

import model.points._
import model.tools.web.result._
import model.tools.web.sitespec.WebElementSpec

object WebGrader {

  private val pointsForElement: Points          = singlePoint
  private val pointForCorrectTextResult: Points = singlePoint
  private val maxPointsForAction: Points        = singlePoint

  private def gradeTextResult(tcr: TextResult): GradedTextResult = {

    val keyName = tcr match {
      case AttributeResult(key, _, _) => key
      case TextContentResult(_, _)    => "TextContent"
    }

    val success = tcr.maybeFoundContent match {
      case None               => false
      case Some(foundContent) => foundContent.contains(tcr.awaitedContent)
    }

    val points = if (success) pointForCorrectTextResult else zeroPoints

    GradedTextResult(keyName, tcr.awaitedContent, tcr.maybeFoundContent, success, points, maxPoints = singlePoint)
  }

  private def calculateMaxPointsForElementSpec[ES <: WebElementSpec](elementSpec: ES): Points = {
    val pointsForTextContentResult: Points = elementSpec.awaitedTextContent match {
      case None    => zeroPoints
      case Some(_) => pointForCorrectTextResult
    }

    val pointsForAttributes: Points = pointForCorrectTextResult * elementSpec.attributes.size

    pointsForElement + pointsForTextContentResult + pointsForAttributes
  }

  private def gradeElementSpecResult(elementSpecResult: ElementSpecResult): GradedElementSpecResult = {

    val maxPoints = calculateMaxPointsForElementSpec(elementSpecResult.elementSpec)

    elementSpecResult match {
      case ElementFoundElementSpecResult(_, _, textContentResult, attributeResults) =>
        val maybeGradedTextContentResult: Option[GradedTextResult] = textContentResult.map(gradeTextResult)

        val gradedAttributeResults: Seq[GradedTextResult] = attributeResults.map(gradeTextResult)

        val pointsForTextContentResult = maybeGradedTextContentResult.map(_.points).getOrElse(zeroPoints)
        val pointsForAttributeResults  = addUp(gradedAttributeResults.map(_.points))

        val points = pointsForElement + pointsForTextContentResult + pointsForAttributeResults

        GradedElementSpecResult(
          elementFound = true,
          maybeGradedTextContentResult,
          gradedAttributeResults,
          points,
          maxPoints
        )

      case _ => GradedElementSpecResult(elementFound = false, None, Seq[GradedTextResult](), zeroPoints, maxPoints)
    }
  }

  private def gradeActionResult(result: JsActionResult): GradedJsActionResult = {
    val success = result match {
      case ElementFoundJsActionResult(_, _, actionPerformed) => actionPerformed
      case _                                                 => false
    }

    val points = if (success) maxPointsForAction else zeroPoints

    GradedJsActionResult(success, result.jsAction, points, maxPointsForAction)
  }

  def gradeHtmlTaskResult(htr: HtmlTaskResult): GradedHtmlTaskResult = GradedHtmlTaskResult(htr.htmlTask.id, gradeElementSpecResult(htr.elementSpecResult))

  def gradeJsTaskResult(jtr: JsTaskResult): GradedJsTaskResult = {

    val gradedPreResults   = jtr.preResults.map { gradeElementSpecResult }
    val gradedActionResult = gradeActionResult(jtr.actionResult)
    val gradedPostResults  = jtr.postResults.map { gradeElementSpecResult }

    val points: Points    = addUp(gradedPreResults.map(_.points)) + gradedActionResult.points + addUp(gradedPostResults.map(_.points))
    val maxPoints: Points = addUp(gradedPreResults.map(_.maxPoints)) + gradedActionResult.maxPoints + addUp(gradedPostResults.map(_.maxPoints))

    GradedJsTaskResult(
      jtr.jsTask.id,
      gradedPreResults,
      gradedActionResult,
      gradedPostResults,
      points,
      maxPoints
    )
  }

}

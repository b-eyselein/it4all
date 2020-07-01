package model.tools.web

import de.uniwue.webtester.result._
import de.uniwue.webtester.sitespec.HtmlElementSpec
import model.core.result.SuccessType
import model.points._

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

  private def calculateMaxPointsForElementSpec[ES <: HtmlElementSpec](elementSpec: ES): Points = {
    val pointsForTextContentResult: Points = elementSpec.awaitedTextContent match {
      case None    => zeroPoints
      case Some(_) => pointForCorrectTextResult
    }

    val pointsForAttributes: Points = pointForCorrectTextResult * elementSpec.attributes.size

    pointsForElement + pointsForTextContentResult + pointsForAttributes
  }

  private def gradeElementSpecResult[GESR <: GradedElementSpecResult](
    elementSpecResult: ElementSpecResult[_ <: HtmlElementSpec],
    applyGradedResult: (
      SuccessType,
      Boolean,
      Option[GradedTextResult],
      Seq[GradedTextResult],
      Boolean,
      Points,
      Points
    ) => GESR
  ): GESR = {
    val maxPoints = calculateMaxPointsForElementSpec(elementSpecResult.elementSpec)

    elementSpecResult match {
      case ElementFoundElementSpecResult(_, _, textContentResult, attributeResults) =>
        val maybeGradedTextContentResult: Option[GradedTextResult] = textContentResult.map(gradeTextResult)

        val gradedAttributeResults: Seq[GradedTextResult] = attributeResults.map(gradeTextResult)

        val pointsForTextContentResult = maybeGradedTextContentResult.map(_.points).getOrElse(zeroPoints)
        val pointsForAttributeResults  = addUp(gradedAttributeResults.map(_.points))

        val points = pointsForElement + pointsForTextContentResult + pointsForAttributeResults

        val allAttributeResultsSuccessful = gradedAttributeResults.forall(_.isSuccessful)

        val successType: SuccessType = if (allAttributeResultsSuccessful) {
          maybeGradedTextContentResult match {
            case None             => SuccessType.COMPLETE
            case Some(textResult) => if (textResult.isSuccessful) SuccessType.COMPLETE else SuccessType.PARTIALLY
          }
        } else SuccessType.PARTIALLY

        val isSuccessful = successType == SuccessType.COMPLETE

        applyGradedResult(
          successType,
          true,
          maybeGradedTextContentResult,
          gradedAttributeResults,
          isSuccessful,
          points,
          maxPoints
        )

      case _ => applyGradedResult(SuccessType.NONE, false, None, Seq[GradedTextResult](), false, zeroPoints, maxPoints)
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

  def gradeHtmlTaskResult(htr: HtmlTaskResult): GradedHtmlTaskResult = gradeElementSpecResult(
    htr.elementSpecResult,
    GradedHtmlTaskResult(htr.elementSpecResult.elementSpec.id, _, _, _, _, _, _, _)
  )

  def gradeJsTaskResult(jtr: JsTaskResult): GradedJsTaskResult = {

    val gradedPreResults = jtr.preResults.map { esr =>
      gradeElementSpecResult(esr, GradedJsHtmlElementSpecResult(esr.elementSpec.id, _, _, _, _, _, _, _))
    }
    val preResultsSuccessful = gradedPreResults.nonEmpty && gradedPreResults.forall(_.isSuccessful)

    val gradedActionResult = gradeActionResult(jtr.actionResult)
    val actionSuccessful   = gradedActionResult.actionPerformed

    val gradedPostResults = jtr.postResults.map { esr =>
      gradeElementSpecResult(esr, GradedJsHtmlElementSpecResult(esr.elementSpec.id, _, _, _, _, _, _, _))
    }
    val postResultsSuccessful = gradedPostResults.nonEmpty && gradedPostResults.forall(_.isSuccessful)

    val points: Points = addUp(gradedPreResults.map(_.points)) + gradedActionResult.points + addUp(
      gradedPostResults.map(_.points)
    )

    val maxPoints: Points = addUp(gradedPreResults.map(_.maxPoints)) + gradedActionResult.maxPoints + addUp(
      gradedPostResults.map(_.maxPoints)
    )

    val success: SuccessType = if (preResultsSuccessful && actionSuccessful && postResultsSuccessful) SuccessType.COMPLETE else SuccessType.PARTIALLY

    GradedJsTaskResult(
      jtr.jsTask.id,
      gradedPreResults,
      gradedActionResult,
      gradedPostResults,
      success,
      points,
      maxPoints
    )
  }

}

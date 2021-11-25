package model.tools.web

import model.tools.web.result._
import org.scalatest.Assertion
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

trait WebCorrectorBasics {
  self: AnyFlatSpec with Matchers =>

  // TextContent-, AttributeResult

  private def evaluateTextResult(textResult: TextResult): Assertion = textResult.maybeFoundContent match {
    case None                    => fail("")
    case Some(gottenTextContent) => assert(gottenTextContent.contains(textResult.awaitedContent), textResult)
  }

  // HtmlElementSpec

  private def evaluateHtmlElementSpecResult(elementSpecResult: ElementSpecResult): Assertion = elementSpecResult match {
    case NoElementFoundElementSpecResult(_)              => fail("Could not find an element!")
    case TooManyElementsFoundElementSpecResult(_, count) => fail(s"Found too many elements ($count)!")

    case ElementFoundElementSpecResult(elementSpec, foundElement, textContentResult, attributeResults) =>
      assert(foundElement.getTagName == elementSpec.awaitedTagName)

      textContentResult.map(evaluateTextResult).getOrElse(1 == 1)

      attributeResults.map(evaluateTextResult).headOption.getOrElse(assert(1 == 1))
  }

  // JsAction

  private def evaluateJsActionResult(jsActionResult: JsActionResult): Assertion = jsActionResult match {
    case NoElementFoundJsActionResult(_)                   => fail("Could not find an element!")
    case TooManyElementsFoundJsActionResult(_, count)      => fail(s"Found too many elements ($count)!")
    case ElementFoundJsActionResult(_, _, actionPerformed) => assert(actionPerformed)
  }

  // HtmlTask

  protected def evaluateHtmlTaskResult(htmlResult: HtmlTaskResult): Assertion = evaluateHtmlElementSpecResult(
    htmlResult.elementSpecResult
  )

  // JsTask

  protected def evaluateJsTaskResult(jsTaskResult: JsTaskResult): Assertion = {

    jsTaskResult.preResults.map(evaluateHtmlElementSpecResult)

    evaluateJsActionResult(jsTaskResult.actionResult)

    jsTaskResult.postResults.map(evaluateHtmlElementSpecResult).head
  }

}

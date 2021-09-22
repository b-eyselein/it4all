package model.tools.web

import model.tools.web.result._
import model.tools.web.sitespec._
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.{By, SearchContext, WebElement}

import scala.jdk.CollectionConverters._
import scala.util.Try

object WebCorrector {

  private def findElementsByXPath(searchContext: SearchContext, xpath: String): List[WebElement] =
    searchContext.findElements(By.xpath(xpath)).asScala.toList

  // HtmlElementSpec

  private def evaluateHtmlElementSpec(
    elementSpec: WebElementSpec,
    searchContext: SearchContext
  ): ElementSpecResult = findElementsByXPath(searchContext, elementSpec.xpathQuery) match {
    case Nil => NoElementFoundElementSpecResult(elementSpec)

    case foundElement :: Nil =>
      val attrResults = elementSpec.attributes.toSeq.map { case (key, value) => evaluateAttribute(key, value, foundElement) }

      val textResult = elementSpec.awaitedTextContent.map(evaluateTextContent(_, foundElement))

      ElementFoundElementSpecResult(elementSpec, foundElement, textResult, attrResults)

    case other => TooManyElementsFoundElementSpecResult(elementSpec, other.size)
  }

  private def evaluateTextContent(awaitedContent: String, element: WebElement): TextContentResult =
    TextContentResult(awaitedContent, Option(element.getAttribute("textContent")))

  private def evaluateAttribute(key: String, value: String, element: WebElement): AttributeResult =
    AttributeResult(key, value, Option(element.getAttribute(key)))

  private def performAction(
    jsAction: JsAction,
    searchContext: SearchContext
  ): JsActionResult = findElementsByXPath(searchContext, jsAction.xpathQuery) match {
    case Nil => NoElementFoundJsActionResult(jsAction)
    case element :: Nil =>
      val actionPerformed = jsAction.actionType match {
        case JsActionType.Click =>
          element.click()
          true
        case JsActionType.FillOut =>
          element.clear()
          element.sendKeys(jsAction.keysToSend getOrElse "")
          // click on other element to fire the onchange event...
          searchContext.findElement(By.xpath("//body")).click()
          true
      }

      ElementFoundJsActionResult(jsAction, element, actionPerformed)

    case other => TooManyElementsFoundJsActionResult(jsAction, other.size)
  }

  // Tasks

  def evaluateHtmlTask(htmlTask: HtmlTask, searchContext: SearchContext): HtmlTaskResult =
    HtmlTaskResult(htmlTask, evaluateHtmlElementSpec(htmlTask.elementSpec, searchContext))

  def evaluateJsTask(jsTask: JsTask, searchContext: SearchContext): JsTaskResult = {

    val preConditionResults = jsTask.preConditions.map(evaluateHtmlElementSpec(_, searchContext))

    val actionResult = performAction(jsTask.action, searchContext)

    val postConditionResults = jsTask.postConditions.map(evaluateHtmlElementSpec(_, searchContext))

    JsTaskResult(jsTask, preConditionResults, actionResult, postConditionResults)
  }

  // Helper method

  private def getDriverWithUrl(url: String): Try[HtmlUnitDriver] = Try {
    val driver = new HtmlUnitDriver(true)
    driver.get(url)
    driver
  }

  // complete site spec

  def evaluateSiteSpec(baseUrl: String, siteSpec: SiteSpec): Try[SiteSpecResult] = {
    val url = s"$baseUrl/${siteSpec.fileName}"

    getDriverWithUrl(url) map { driver =>
      val htmlTaskResults = siteSpec.htmlTasks.map(evaluateHtmlTask(_, driver))

      val jsTaskResults = siteSpec.jsTasks.map(evaluateJsTask(_, driver))

      result.SiteSpecResult(htmlTaskResults, jsTaskResults)
    }
  }

}

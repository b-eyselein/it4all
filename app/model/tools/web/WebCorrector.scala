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

  private def evaluateHtmlElementSpec(elementSpec: WebElementSpec, searchContext: SearchContext): ElementSpecResult = {

    val WebElementSpec(xpathQuery, _ /* awaitedTagName */, awaitedTextContent, attributes) = elementSpec

    findElementsByXPath(searchContext, xpathQuery) match {
      case Nil => NoElementFoundElementSpecResult(elementSpec)

      case foundElement :: Nil =>
        ElementFoundElementSpecResult(
          elementSpec,
          foundElement,
          textContentResult = awaitedTextContent.map { evaluateTextContent(_, foundElement) },
          attributeResults = attributes.toSeq.map { case (key, value) => evaluateAttribute(key, value, foundElement) }
        )

      case other => TooManyElementsFoundElementSpecResult(elementSpec, other.size)
    }
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

  def evaluateJsTask(jsTask: JsTask, searchContext: SearchContext): JsTaskResult = JsTaskResult(
    jsTask,
    preResults = jsTask.preConditions.map(evaluateHtmlElementSpec(_, searchContext)),
    actionResult = performAction(jsTask.action, searchContext),
    postResults = jsTask.postConditions.map(evaluateHtmlElementSpec(_, searchContext))
  )

  // complete site spec

  def evaluateSiteSpec(baseUrl: String, siteSpec: SiteSpec): Try[SiteSpecResult] = Try {
    val driver = new HtmlUnitDriver(true)

    driver.get(s"$baseUrl/${siteSpec.fileName}")

    SiteSpecResult(
      htmlResults = siteSpec.htmlTasks.map(evaluateHtmlTask(_, driver)),
      jsResults = siteSpec.jsTasks.map(evaluateJsTask(_, driver))
    )
  }

}

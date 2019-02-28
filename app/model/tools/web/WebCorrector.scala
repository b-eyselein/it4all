package model.tools.web

import model.core.result.SuccessType
import org.openqa.selenium.{By, SearchContext, WebElement}

import scala.collection.JavaConverters.asScalaBufferConverter

object WebCorrector {

  private def findElementsByXPath(searchContext: SearchContext, xpath: String): List[WebElement] =
    searchContext.findElements(By.xpath(xpath)).asScala.toList

  def evaluateHtmlTask(htmlTask: HtmlTask, searchContext: SearchContext): ElementResult =
    findElementsByXPath(searchContext, htmlTask.xpathQuery) match {
      case Nil => ElementResult(htmlTask, None, Seq[AttributeResult](), None)

      case foundElement :: Nil =>
        val attrResults = htmlTask.attributes map (evaluateAttribute(_, foundElement))
        val textResult = htmlTask.textContent map (TextContentResult(Option(foundElement.getAttribute("textContent")), _))

        ElementResult(htmlTask, Some(foundElement), attrResults, textResult)

      case _ :: _ => ElementResult(htmlTask, None, Seq[AttributeResult](), None)
    }

  private def evaluateAttribute(attribute: HtmlAttribute, element: WebElement): AttributeResult =
    AttributeResult(attribute, Option(element.getAttribute(attribute.key)))

  def evaluateJsTask(jsTask: JsTask, searchContext: SearchContext): JsWebResult = {
    val (preConds, postConds) = jsTask.conditions.partition(_.isPrecondition)

    JsWebResult(
      jsTask,
      preConds map (testCondition(_, searchContext)),
      jsTask.perform(searchContext),
      postConds map (testCondition(_, searchContext))
    )
  }

  private def testCondition(condition: JsCondition, searchContext: SearchContext): ConditionResult =
    findElementsByXPath(searchContext, condition.xpathQuery) match {
      case Nil            => ConditionResult(SuccessType.NONE, condition, None)
      case element :: Nil => ConditionResult(SuccessType.ofBool(element.getText == condition.awaitedValue), condition, Some(element.getText))
      case _ :: _         => ConditionResult(SuccessType.NONE, condition, None)
    }

}

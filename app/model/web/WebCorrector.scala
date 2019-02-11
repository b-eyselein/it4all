package model.web

import model.core.result.SuccessType
import org.openqa.selenium.{By, SearchContext, WebElement}

import scala.collection.JavaConverters.asScalaBufferConverter

object WebCorrector {

  private def findElementsByXPath(searchContext: SearchContext, xpath: String): List[WebElement] =
    searchContext.findElements(By.xpath(xpath)).asScala.toList

  def evaluateHtmlTask(completeHtmlTask: HtmlCompleteTask, searchContext: SearchContext): ElementResult =
    findElementsByXPath(searchContext, completeHtmlTask.task.xpathQuery) match {
      case Nil => ElementResult(completeHtmlTask, None, Seq[AttributeResult](), None)

      case foundElement :: Nil =>
        val attrResults = completeHtmlTask.attributes map (evaluateAttribute(_, foundElement))
        val textResult = completeHtmlTask.task.textContent map (TextContentResult(foundElement.getAttribute("textContent"), _))

        ElementResult(completeHtmlTask, Some(foundElement), attrResults, textResult)

      case _ :: _ => ElementResult(completeHtmlTask, None, Seq[AttributeResult](), None)
    }

  private def evaluateAttribute(attribute: Attribute, element: WebElement): AttributeResult =
    AttributeResult(attribute, Option(element.getAttribute(attribute.key)))

  def evaluateJsTask(completeJsTask: JsCompleteTask, searchContext: SearchContext): JsWebResult = {
    val (preConds, postConds) = completeJsTask.conditions.partition(_.isPrecondition)

    JsWebResult(
      completeJsTask,
      preConds map (testCondition(_, searchContext)),
      completeJsTask.task.perform(searchContext),
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
package model.web

import model.core.result.SuccessType
import org.openqa.selenium.{By, SearchContext, WebElement}

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.util.{Failure, Success, Try}

object WebCorrector {

  def evaluateWebTask(task: WebCompleteTask, searchContent: SearchContext): WebResult = task match {
    case task: HtmlCompleteTask => evaluateHtmlTask(task, searchContent)
    case task: JsCompleteTask   => evaluateJsTask(task, searchContent)
  }

   def evaluateHtmlTask(completeHtmlTask: HtmlCompleteTask, searchContext: SearchContext): ElementResult =
    searchContext.findElements(By.xpath(completeHtmlTask.task.xpathQuery)).asScala.toList match {
      case Nil => ElementResult(completeHtmlTask, None, Seq[AttributeResult](), None)

      case foundElement :: Nil =>
        val attrResults = completeHtmlTask.attributes map (evaluateAttribute(_, foundElement))
        val textResult = completeHtmlTask.task.textContent map (TextContentResult(foundElement.getText, _))
        ElementResult(completeHtmlTask, Some(foundElement), attrResults, textResult)

      case _ :: _ => ElementResult(completeHtmlTask, None, Seq[AttributeResult](), None)
    }

  private def evaluateAttribute(attribute: Attribute, element: WebElement): AttributeResult =
    AttributeResult(attribute, Try(element.getAttribute(attribute.key)).toOption)

  def evaluateJsTask(completeJsTask: JsCompleteTask, searchContext: SearchContext): JsWebResult = {
    val (preconds, postconds) = completeJsTask.conditions.partition(_.isPrecondition)

    JsWebResult(
      completeJsTask,
      preconds map (testCondition(_, searchContext)),
      completeJsTask.task.perform(searchContext),
      postconds map (testCondition(_, searchContext))
    )
  }

  private def testCondition(condition: JsCondition, searchContext: SearchContext): ConditionResult = Try(searchContext findElement By.xpath(condition.xpathQuery)) match {
    case Failure(_)       => ConditionResult(SuccessType.NONE, condition, None)
    case Success(element) => ConditionResult(SuccessType.ofBool(element.getText == condition.awaitedValue), condition, Some(element.getText))
  }

}
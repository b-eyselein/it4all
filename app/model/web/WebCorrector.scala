package model.web

import model.Enums.SuccessType
import model.Enums.SuccessType._
import org.openqa.selenium.{By, SearchContext, WebElement}

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.util.{Failure, Success, Try}

object WebCorrector {

  def evaluate(task: WebCompleteTask, searchContent: SearchContext): WebResult = task match {
    case task: HtmlCompleteTask => evaluateHtmlTask(task, searchContent)
    case task: JsCompleteTask   => evaluateJsTask(task, searchContent)
  }

  private def evaluateHtmlTask(completeHtmlTask: HtmlCompleteTask, searchContext: SearchContext): ElementResult =
    searchContext.findElements(By.xpath(completeHtmlTask.task.xpathQuery)).asScala.toList match {
      case Nil => ElementResult(completeHtmlTask, None, Seq.empty, None)

      case foundElement :: Nil =>
        val attrResults = completeHtmlTask.attributes map (evaluateAttribute(_, foundElement))
        val textResult = completeHtmlTask.task.textContent map (TextContentResult(foundElement.getText, _))
        ElementResult(completeHtmlTask, Some(foundElement), attrResults, textResult)

      case _ :: _ => ElementResult(completeHtmlTask, None, Seq.empty, None)
    }

  private def evaluateAttribute(attribute: Attribute, element: WebElement): AttributeResult =
    AttributeResult(attribute, Try(element getAttribute attribute.key))

  private def evaluateConditions(context: SearchContext, conditions: Seq[JsCondition]): Seq[ConditionResult] =
    conditions map (testCondition(_, context))

  private def evaluateJsTask(completeJsTask: JsCompleteTask, searchContext: SearchContext) =
    JsWebResult(
      completeJsTask,
      evaluateConditions(searchContext, completeJsTask.conditions filter (_.isPrecondition)),
      completeJsTask.task.perform(searchContext),
      evaluateConditions(searchContext, completeJsTask.conditions filter (!_.isPrecondition))
    )

  private def testCondition(condition: JsCondition, searchContext: SearchContext): ConditionResult = Try(searchContext findElement By.xpath(condition.xpathQuery)) match {
    case Failure(_)       => ConditionResult(NONE, condition, null)
    case Success(element) =>
      val gottenValue = element.getText
      ConditionResult(SuccessType.ofBool(gottenValue == condition.awaitedValue), condition, gottenValue)
  }

}
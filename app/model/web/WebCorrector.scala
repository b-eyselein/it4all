package model.web

import model.core.result.SuccessType
import org.openqa.selenium.{By, SearchContext, WebElement}

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.util.{Failure, Success, Try}

object WebCorrector {

  def evaluate(task: DbWebTask, searchContent: SearchContext): WebResult = task match {
    case task: DbHtmlTask => evaluateHtmlTask(task, searchContent)
    case task: DbJsTask   => evaluateJsTask(task, searchContent)
  }

  def evaluateHtmlTask(task: DbHtmlTask, searchContext: SearchContext): ElementResult = {
    val foundElements = searchContext.findElements(By.xpath(task.xpathQuery)).asScala.toList

    val foundElement = foundElements match {
      case Nil            => None
      case foundEl :: Nil => Some(foundEl)
      case _ :: _         => None
    }

    val (attrResults, textResult) = foundElements match {
      case Nil                      => (List.empty, None)
      case firstFoundElement :: Nil => (task.getAttributes.map(evaluateAttribute(_, firstFoundElement)), Option(TextContentResult(firstFoundElement.getText, task.textContent)))
      case _ :: _                   => (List.empty, None)
    }

    new ElementResult(task, foundElement, attrResults, textResult)
  }

  def evaluateAttribute(attribute: Attribute, element: WebElement): AttributeResult =
    AttributeResult(attribute, Try(element.getAttribute(attribute.key)))

  def evaluateConditions(context: SearchContext, conditions: List[DbJsCondition]): List[ConditionResult] = conditions.map(testCondition(_, context))

  def evaluateJsTask(task: DbJsTask, searchContext: SearchContext) =
    new JsWebResult(
      task,
      evaluateConditions(searchContext, task.conditions.filter(_.isPrecondition)),
      task.action == null || task.action.perform(searchContext),
      evaluateConditions(searchContext, task.conditions.filter(!_.isPrecondition)),
      List.empty)

  def testCondition(condition: DbJsCondition, searchContext: SearchContext): ConditionResult = Try(searchContext.findElement(By.xpath(condition.xpathQuery))) match {
    case Failure(_)       => ConditionResult(SuccessType.NONE, condition, null)
    case Success(element) =>
      val gottenValue = element.getText
      val success = if (gottenValue.equals(condition.awaitedValue)) SuccessType.COMPLETE else SuccessType.NONE
      ConditionResult(success, condition, gottenValue)
  }

}